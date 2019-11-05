package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.StockCalculator;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;

class StockCalculatorImplTest {

    private StockCalculator stockCalculator = new StockCalculatorImpl();

    @Test
    void calculateLatestPriceWithAllocationEmpty() {
        BigDecimal latestPrice = BigDecimal.valueOf(2);
        BigDecimal volume = TEN;

        BigDecimal newLatestPrice =
                stockCalculator.calculateLatestPrice(Optional.empty(), volume, latestPrice);

        assertEquals(newLatestPrice, latestPrice.multiply(volume));
    }

    @Test
    void calculateLatestPriceWithAllocation() {
        BigDecimal latestPrice = BigDecimal.valueOf(3);
        BigDecimal volume = TEN;
        BigDecimal assetValue = ONE;
        Optional<AllocationResponse> optionalAllocationResponse =
                Optional.of(new AllocationResponse(null, assetValue, null));

        BigDecimal actualNewLatestPrice =
                stockCalculator.calculateLatestPrice(optionalAllocationResponse, volume, latestPrice);

        BigDecimal expectedLatestPrice = latestPrice.multiply(volume).add(assetValue);
        assertEquals(actualNewLatestPrice, expectedLatestPrice);
    }

    @Test
    void calculateSum() {
        BigDecimal firstValue = ONE;
        AllocationResponse firstAllocationResponse = AllocationResponse.builder()
                .assetValue(firstValue)
                .build();
        BigDecimal secondValue = BigDecimal.valueOf(3);
        AllocationResponse secondAllocationResponse = AllocationResponse.builder()
                .assetValue(secondValue)
                .build();

        BigDecimal sum = stockCalculator.calculateSum(
                Arrays.asList(firstAllocationResponse, secondAllocationResponse));

        assertEquals(firstValue.add(secondValue), sum);
    }

    @Test
    void enrichProportion() {
        BigDecimal firstValue = ONE;
        AllocationResponse firstAllocationResponse = AllocationResponse.builder()
                .assetValue(firstValue)
                .build();
        BigDecimal secondValue = BigDecimal.valueOf(3);
        AllocationResponse secondAllocationResponse = AllocationResponse.builder()
                .assetValue(secondValue)
                .build();
        BigDecimal sum = firstValue.add(secondValue);

        List<AllocationResponse> allocationResponses = stockCalculator.enrichProportion(
                Arrays.asList(firstAllocationResponse, secondAllocationResponse), sum);

        assertEquals(25, allocationResponses.get(0).getProportion().intValue());
        assertEquals(75, allocationResponses.get(1).getProportion().intValue());
    }
}