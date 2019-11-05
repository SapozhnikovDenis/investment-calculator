package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.StockCalculator;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

@Service
public class StockCalculatorImpl implements StockCalculator {

    private static final int SCALE_FOR_PROPORTION = 3;
    private static final int ONE_HUNDRED = 100;

    public BigDecimal calculateLatestPrice(Optional<AllocationResponse> optionalAllocation,
                                            BigDecimal volume, BigDecimal latestPrice) {
        BigDecimal newLatestPrice = sutTrailingZerosAndScale(latestPrice.multiply(volume));
        if (optionalAllocation.isPresent()) {
            newLatestPrice = optionalAllocation.get().getAssetValue().add(newLatestPrice);
        }
        return newLatestPrice;
    }

    public BigDecimal calculateSum(List<AllocationResponse> allocationResponses) {
        return sutTrailingZerosAndScale(allocationResponses.stream()
                .map(AllocationResponse::getAssetValue)
                .reduce(BigDecimal::add)
                .orElse(ZERO));
    }

    public List<AllocationResponse> enrichProportion(List<AllocationResponse> allocations, BigDecimal sum) {
        return allocations.stream()
                .map(allocationResponse -> enrichProportion(allocationResponse, sum))
                .collect(Collectors.toList());
    }

    private AllocationResponse enrichProportion(AllocationResponse allocation, BigDecimal sum) {
        BigDecimal proportion = calculateProportion(sum, allocation.getAssetValue());
        return allocation.toBuilder()
                .proportion(proportion)
                .build();
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
