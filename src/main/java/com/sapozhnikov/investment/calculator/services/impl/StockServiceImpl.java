package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.StockCalculator;
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

    private final FinancialService financialService;
    private final StockCalculator stockCalculator;

    @Override
    public StocksCostCalculateResponse calculateCost(StocksCostCalculateRequest stocksCostCalculateRequest) {
        Set<StockRequest> setStockRequests = new HashSet<>(stocksCostCalculateRequest.getStocks());
        Map<StockRequest, StockInfoInternal> stockInfos = financialService.getStockInfo(setStockRequests);
        List<AllocationResponse> allocationResponses = getAllocationResponses(stockInfos);
        BigDecimal sum = stockCalculator.calculateSum(allocationResponses);
        List<AllocationResponse> allocationResponsesWithProportion =
                stockCalculator.enrichProportion(allocationResponses, sum);
        return new StocksCostCalculateResponse(sum, allocationResponsesWithProportion);
    }

    private List<AllocationResponse> getAllocationResponses(Map<StockRequest, StockInfoInternal> stockInfos) {
        Map<String, AllocationResponse> allocationsGroupBySector = new HashMap<>();
        for (StockRequest stockRequest : stockInfos.keySet()) {
            StockInfoInternal stockInfo = stockInfos.get(stockRequest);
            String sector = stockInfo.getSector();
            Optional<AllocationResponse> optionalAllocationResponse =
                    Optional.ofNullable(allocationsGroupBySector.get(sector));
            BigDecimal latestPrice = stockCalculator.calculateLatestPrice(
                    optionalAllocationResponse, stockRequest.getVolume(), stockInfo.getLatestPrice());
            AllocationResponse allocationResponse = buildAllocation(sector, latestPrice);
            allocationsGroupBySector.put(sector, allocationResponse);
        }
        return new ArrayList<>(allocationsGroupBySector.values());
    }

    private AllocationResponse buildAllocation(String sector, BigDecimal latestPrice) {
        return AllocationResponse.builder()
                        .sector(sector)
                        .assetValue(latestPrice)
                        .build();
    }
}
