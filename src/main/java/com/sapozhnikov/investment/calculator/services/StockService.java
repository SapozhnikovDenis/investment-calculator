package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.SectorWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithLatestPrice;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithSector;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final FinancialService financialService;
    private final StockMatcher stockMatcher;
    private final Calculator calculator;

    public StocksCostCalculateResponse calculateCost(StocksCostCalculateRequest stocksCostCalculateRequest) {
        List<SectorWithAssetValue> listSectorWithAssetValue = getSectorWithAssertValues(stocksCostCalculateRequest);
        BigDecimal sumAllStockValue = calculateSumAllStockValue(listSectorWithAssetValue);
        List<AllocationResponse> listAllocationResponse =
                buildListAllocationResponse(listSectorWithAssetValue, sumAllStockValue);
        return new StocksCostCalculateResponse(sumAllStockValue, listAllocationResponse);
    }

    private List<SectorWithAssetValue> getSectorWithAssertValues(StocksCostCalculateRequest stocksCostCalculateRequest) {
        List<StockRequest> stocks = stocksCostCalculateRequest.getStocks();
        List<String> symbols = stocks.stream()
                .map(StockRequest::getSymbol)
                .collect(Collectors.toList());
        List<SymbolWithLatestPrice> listSymbolWithLatestPrice = financialService.getLatestPrice(symbols);
        List<SymbolWithSector> listSymbolWithSector = financialService.getSector(symbols);
        List<SymbolWithAssetValue> listSymbolWithAssetValue =
                calculateAssetValue(stocks, listSymbolWithLatestPrice);
        List<SectorWithAssetValue> listSectorWithAssetValue =
                stockMatcher.matchSectorWithAssetValueBySymbol(listSymbolWithSector, listSymbolWithAssetValue);
        return stockMatcher.matchSectorWithAssetValueBySectors(listSectorWithAssetValue);
    }

    private List<SymbolWithAssetValue> calculateAssetValue(List<StockRequest> stocks,
                                                           List<SymbolWithLatestPrice> listSymbolWithLatestPrice) {
        Map<SymbolWithLatestPrice, BigDecimal> symbolWithLatestPriceMatchVolume =
                stockMatcher.matchLatestPriceWithVolumeBySymbol(listSymbolWithLatestPrice, stocks);
        return multiplyLatestPriceOnVolume(symbolWithLatestPriceMatchVolume);
    }

    private BigDecimal calculateSumAllStockValue(List<SectorWithAssetValue> listSectorWithAssetValue) {
        List<BigDecimal> allAssertValue = listSectorWithAssetValue.stream()
                .map(SectorWithAssetValue::getAssetValue)
                .collect(Collectors.toList());
        return calculator.calculateSum(allAssertValue);
    }

    private List<AllocationResponse> buildListAllocationResponse(List<SectorWithAssetValue> listSectorWithAssetValue,
                                                                 BigDecimal sumAllStockValue) {
        List<AllocationResponse> listAllocationResponse = new ArrayList<>();
        BigDecimal sumProportion = ZERO;
        int listSectorWithAssetValueSize = listSectorWithAssetValue.size();
        for (int i = 0; i < listSectorWithAssetValueSize; i++) {
            SectorWithAssetValue sectorWithAssetValue = listSectorWithAssetValue.get(i);
            String sector = sectorWithAssetValue.getSector();
            BigDecimal assertValue = sectorWithAssetValue.getAssetValue();
            BigDecimal proportion;
            if ((listSectorWithAssetValueSize - 1) == i) {
                proportion = ONE_HUNDRED.subtract(sumProportion);
            } else {
                proportion = calculator.calculateProportion(sumAllStockValue, assertValue);
                sumProportion = sumProportion.add(proportion);
            }
            AllocationResponse allocationResponse = new AllocationResponse(sector, assertValue, proportion);
            listAllocationResponse.add(allocationResponse);
        }
        return listAllocationResponse;
    }

    private List<SymbolWithAssetValue> multiplyLatestPriceOnVolume(
            Map<SymbolWithLatestPrice, BigDecimal> symbolWithLatestPriceMatchVolume) {
        List<SymbolWithAssetValue> listSymbolWithAssetValue = new ArrayList<>();
        symbolWithLatestPriceMatchVolume.forEach((symbolWithLatestPrice, volume) -> {
            BigDecimal assetValue = symbolWithLatestPrice.getLatestPrice().multiply(volume);
            SymbolWithAssetValue symbolWithAssetValue =
                    new SymbolWithAssetValue(symbolWithLatestPrice.getSymbol(), assetValue);
            listSymbolWithAssetValue.add(symbolWithAssetValue);
        });
        return listSymbolWithAssetValue;
    }
}
