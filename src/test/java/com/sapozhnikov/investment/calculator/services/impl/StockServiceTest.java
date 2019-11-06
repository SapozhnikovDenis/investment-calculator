package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.Calculator;
import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.StockMatcher;
import com.sapozhnikov.investment.calculator.services.StockService;
import com.sapozhnikov.investment.calculator.services.dto.internal.SectorWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.StockInfoInternal;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithLatestPrice;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithSector;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StockServiceTest {

    private FinancialService financialService;
    private StockMatcher stockMatcher;
    private StockService stockService;
    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        financialService = mock(FinancialService.class);
        calculator = mock(Calculator.class);
        stockMatcher = mock(StockMatcher.class);
        stockService = new StockService(financialService, stockMatcher,calculator);
    }

    @Test
    void testCalculateCostWithOneStockInfo() {
        BigDecimal sum = TEN;
        String symbol = "A";
        String sector = "B";
        BigDecimal proportion = valueOf(100);
        BigDecimal volume = ONE;
        BigDecimal latestPrice = ONE;
        given(financialService.getLatestPrice(anyList()))
                .willReturn(Collections.singletonList(new SymbolWithLatestPrice(symbol, latestPrice)));
        given(financialService.getSector(anyList()))
                .willReturn(Collections.singletonList(new SymbolWithSector(symbol, sector)));
        given(calculator.calculateSum(any()))
                .willReturn(sum);
        given(calculator.calculateProportion(any(), any()))
                .willReturn(proportion);
        given(stockMatcher.matchSectorWithAssetValueBySectors(any()))
                .willReturn(Collections.singletonList(new SectorWithAssetValue(sector, latestPrice)));
        StocksCostCalculateRequest stocksCostCalculateRequest =
                new StocksCostCalculateRequest(Collections.singletonList(new StockRequest(symbol, volume)));

        StocksCostCalculateResponse stocksCostCalculateResponse =
                stockService.calculateCost(stocksCostCalculateRequest);

        assertThat(stocksCostCalculateResponse.getValue(), is(sum));
        List<AllocationResponse> allocations = stocksCostCalculateResponse.getAllocations();
        AllocationResponse allocationResponse = allocations.get(0);
        assertThat(allocationResponse.getProportion(), is(proportion));
        assertThat(allocationResponse.getAssetValue(), is(volume.multiply(latestPrice)));
    }
}
