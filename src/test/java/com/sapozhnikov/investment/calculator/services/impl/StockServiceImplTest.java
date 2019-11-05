package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.StockCalculator;
import com.sapozhnikov.investment.calculator.services.StockService;
import com.sapozhnikov.investment.calculator.services.dto.internal.StockInfoInternal;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StockServiceImplTest {

    private FinancialService financialService;
    private StockService stockService;
    private StockCalculator stockCalculator;

    @BeforeEach
    public void setUp() {
        financialService = mock(FinancialService.class);
        stockCalculator = mock(StockCalculator.class);
        stockService = new StockServiceImpl(financialService, stockCalculator);
    }

    @Test
    void testCalculateCostWithOneStockInfo() {
        BigDecimal sum = TEN;
        ArrayList<AllocationResponse> allocations = new ArrayList<>();
        given(financialService.getStockInfo(anySet()))
                .willReturn(Map.of(new StockRequest("A", ONE), new StockInfoInternal()));
        given(stockCalculator.enrichProportion(any(), any()))
                .willReturn(allocations);
        given(stockCalculator.calculateSum(any()))
                .willReturn(sum);

        StocksCostCalculateResponse stocksCostCalculateResponse =
                stockService.calculateCost(new StocksCostCalculateRequest(Collections.emptyList()));

        assertThat(stocksCostCalculateResponse.getValue(), is(sum));
        assertThat(stocksCostCalculateResponse.getAllocations(), is(allocations));

    }

    @Test
    void testCalculateCostWithTwoStockInfoByOneSector() {
        BigDecimal sum = TEN;
        ArrayList<AllocationResponse> allocations = new ArrayList<>();
        StockInfoInternal stockInfoInternal = new StockInfoInternal();
        stockInfoInternal.setSector("SECTOR");
        Map<StockRequest, StockInfoInternal> infoInternalMap =
                Map.of(new StockRequest("A", ONE), stockInfoInternal,
                        new StockRequest("B", ZERO), stockInfoInternal);
        given(financialService.getStockInfo(anySet()))
                .willReturn(infoInternalMap);
        given(stockCalculator.enrichProportion(any(), any()))
                .willReturn(allocations);
        given(stockCalculator.calculateSum(any()))
                .willReturn(sum);

        StocksCostCalculateResponse stocksCostCalculateResponse =
                stockService.calculateCost(new StocksCostCalculateRequest(Collections.emptyList()));

        assertThat(stocksCostCalculateResponse.getValue(), is(sum));
        assertThat(stocksCostCalculateResponse.getAllocations(), is(allocations));
    }
}
