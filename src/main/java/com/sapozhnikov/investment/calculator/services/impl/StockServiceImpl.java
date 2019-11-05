package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.StockService;
import com.sapozhnikov.investment.calculator.services.dto.internal.StockInfoInternal;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private static final int SCALE_FOR_PROPORTION = 3;
    private static final int ONE_HUNDRED = 100;

    private final FinancialService financialService;

    @Override
    public StocksCostCalculateResponse calculateCost(StocksCostCalculateRequest stocksCostCalculateRequest) {
        var allocationResponses = getAllocations(stocksCostCalculateRequest.getStocks());
        var sum = calculateSum(allocationResponses);
        enrichProportion(allocationResponses, sum);
        return new StocksCostCalculateResponse(sum, allocationResponses);
    }

    private List<AllocationResponse> getAllocations(List<StockRequest> stockRequests) {
        var stockInfos = financialService.getStockInfo(new HashSet<>(stockRequests));
        Map<String, AllocationResponse> allocationsGroupBySector = new HashMap<>();
        for (StockRequest stockRequest : stockInfos.keySet()) {
            StockInfoInternal stockInfo = stockInfos.get(stockRequest);
            var sector = stockInfo.getSector();
            Optional<AllocationResponse> optionalAllocationResponse =
                    Optional.ofNullable(allocationsGroupBySector.get(sector));
            var latestPrice = calculateLatestPrice(
                    optionalAllocationResponse, stockRequest.getVolume(), stockInfo.getLatestPrice());
            var allocationResponse = new AllocationResponse(sector, latestPrice, ZERO);
            allocationsGroupBySector.put(sector, allocationResponse);
        }
        return new ArrayList<>(allocationsGroupBySector.values());
    }

    private BigDecimal calculateLatestPrice(Optional<AllocationResponse> optionalAllocation,
                                            BigDecimal volume, BigDecimal latestPrice) {
        latestPrice = sutTrailingZerosAndScale(latestPrice.multiply(volume));
        return optionalAllocation.isPresent() ?
                optionalAllocation.get().getAssetValue().add(latestPrice)
                : latestPrice;
    }

    private BigDecimal calculateSum(List<AllocationResponse> allocationResponses) {
        return sutTrailingZerosAndScale(allocationResponses
                .stream()
                .map(AllocationResponse::getAssetValue)
                .reduce(BigDecimal::add)
                .orElse(ZERO));
    }

    private void enrichProportion(List<AllocationResponse> allocations, BigDecimal sum) {
        allocations.forEach(allocationResponse -> enrichProportion(allocationResponse, sum));
    }

    private void enrichProportion(AllocationResponse allocation, BigDecimal sum) {
        allocation.setProportion(calculateProportion(sum, allocation.getAssetValue()));
    }

    private BigDecimal calculateProportion(BigDecimal sum, BigDecimal assetValue) {
        double proportion = assetValue.doubleValue() * ONE_HUNDRED / sum.doubleValue();
        return sutTrailingZerosAndScale(new BigDecimal(proportion));
    }

    private BigDecimal sutTrailingZerosAndScale(BigDecimal value) {
        return new BigDecimal(value.setScale(SCALE_FOR_PROPORTION, RoundingMode.HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString());
    }
}
