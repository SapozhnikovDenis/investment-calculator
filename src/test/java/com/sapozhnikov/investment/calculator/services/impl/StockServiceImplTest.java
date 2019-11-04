package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.FinancialService;
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

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StockServiceImplTest {

    private FinancialService financialService;
    private StockService stockService;

    @BeforeEach
    public void setUp() {
        financialService = mock(FinancialService.class);
        stockService = new StockServiceImpl(financialService);
    }

    @Test
    void testCalculateCostOneSelector() {
        String sector = "AA";
        BigDecimal firstLatestPrice = ONE;
        String firstSymbol = "A";
        StockInfoInternal firstStockInfoInternal = new StockInfoInternal(firstSymbol, sector, firstLatestPrice);
        String secondSymbol = "B";
        BigDecimal secondLatestPrice = TEN;
        StockInfoInternal secondStockInfoInternal = new StockInfoInternal(secondSymbol, sector, secondLatestPrice);
        List<StockInfoInternal> stockInfoInternals = Arrays.asList(firstStockInfoInternal, secondStockInfoInternal);
        StockRequest firstStockRequest = new StockRequest(firstSymbol, ONE);
        StockRequest secondStockRequest = new StockRequest(secondSymbol, ONE);
        given(financialService.getStockInfo(anySet()))
                .willReturn(Map.of(firstStockRequest, firstStockInfoInternal,
                        secondStockRequest, secondStockInfoInternal));
        List<StockRequest> stocks = Arrays.asList(firstStockRequest, secondStockRequest);

        StocksCostCalculateResponse stocksCostCalculateResponse =
                stockService.calculateCost(new StocksCostCalculateRequest(stocks));

        assertThat(stocksCostCalculateResponse.getValue().intValue(), is(getSum(stockInfoInternals, stocks)));
        AllocationResponse allocationResponse = stocksCostCalculateResponse.getAllocations().get(0);
        assertThat(allocationResponse.getSector(), is(sector));
        assertThat(allocationResponse.getAssetValue(), is(firstLatestPrice.add(secondLatestPrice)));
        assertThat(allocationResponse.getProportion(), is(new BigDecimal(100)));
    }

    @Test
    void testCalculateCostOneSelectorWithTwoVolumes() {
        String symbol = "A";
        String sector = "AA";
        BigDecimal firstLatestPrice = ONE;
        BigDecimal volume = new BigDecimal(2);
        StockInfoInternal stockInfoInternal = new StockInfoInternal(symbol, sector, firstLatestPrice);
        List<StockInfoInternal> stockInfoInternals =
                Collections.singletonList(stockInfoInternal);
        StockRequest stockRequest = new StockRequest(symbol, volume);
        given(financialService.getStockInfo(anySet()))
                .willReturn(Map.of(stockRequest, stockInfoInternal));
        List<StockRequest> stocks = Collections.singletonList(stockRequest);

        StocksCostCalculateResponse stocksCostCalculateResponse =
                stockService.calculateCost(new StocksCostCalculateRequest(stocks));

        assertThat(stocksCostCalculateResponse.getValue().intValue(), is(getSum(stockInfoInternals, stocks)));
        AllocationResponse allocationResponse = stocksCostCalculateResponse.getAllocations().get(0);
        assertThat(allocationResponse.getSector(), is(sector));
        assertThat(allocationResponse.getAssetValue(), is(firstLatestPrice.multiply(volume)));
        assertThat(allocationResponse.getProportion(), is(new BigDecimal(100)));
    }

    @Test
    void testCalculateCostTwoSelector() {
        ArrayList<StockInfoInternal> stockInfoInternals = new ArrayList<>();
        String firstSelector = "AA";
        String secondSelector = "BB";
        BigDecimal firstLatestPrice = ONE;
        BigDecimal secondLatestPrice = TEN;
        String firstSymbol = "A";
        stockInfoInternals.add(new StockInfoInternal(firstSymbol, firstSelector, firstLatestPrice));
        String secondSymbol = "B";
        stockInfoInternals.add(new StockInfoInternal(secondSymbol, firstSelector, secondLatestPrice));
        String thirdSymbol = "C";
        stockInfoInternals.add(new StockInfoInternal(thirdSymbol, secondSelector, firstLatestPrice));
        String fourthSelector = "D";
        stockInfoInternals.add(new StockInfoInternal(fourthSelector, secondSelector, secondLatestPrice));
        StockRequest firstStockRequest = new StockRequest(firstSymbol, ONE);
        StockRequest secondStockRequest = new StockRequest(secondSymbol, ONE);
        StockRequest thirdStockRequest = new StockRequest(thirdSymbol, ONE);
        StockRequest fourthStockRequest = new StockRequest(fourthSelector, ONE);
        given(financialService.getStockInfo(anySet()))
                .willReturn(Map.of(
                        firstStockRequest, stockInfoInternals.get(0),
                        secondStockRequest, stockInfoInternals.get(1),
                        thirdStockRequest, stockInfoInternals.get(2),
                        fourthStockRequest, stockInfoInternals.get(3)));
        List<StockRequest> stocks = Arrays.asList(firstStockRequest, secondStockRequest,
                thirdStockRequest, fourthStockRequest);


        StocksCostCalculateResponse stocksCostCalculateResponse =
                stockService.calculateCost(new StocksCostCalculateRequest(stocks));

        assertThat(stocksCostCalculateResponse.getValue().intValue(), is(getSum(stockInfoInternals, stocks)));
        AllocationResponse firstAllocationResponse = stocksCostCalculateResponse.getAllocations().stream()
                .filter(allocationResponse -> allocationResponse.getSector().equals(firstSelector))
                .findFirst()
                .get();
        assertThat(firstAllocationResponse.getSector(), is(firstSelector));
        assertThat(firstAllocationResponse.getAssetValue(), is(firstLatestPrice.add(secondLatestPrice)));
        assertThat(firstAllocationResponse.getProportion(), is(new BigDecimal(50)));
        AllocationResponse secondAllocationResponse = stocksCostCalculateResponse.getAllocations().stream()
                .filter(allocationResponse -> allocationResponse.getSector().equals(secondSelector))
                .findFirst()
                .get();;
        assertThat(secondAllocationResponse.getSector(), is(secondSelector));
        assertThat(secondAllocationResponse.getAssetValue(), is(firstLatestPrice.add(secondLatestPrice)));
        assertThat(secondAllocationResponse.getProportion(), is(new BigDecimal(50)));
    }

    private int getSum(List<StockInfoInternal> stockInfoInternals, List<StockRequest> stockRequests) {
        int sum = 0;
        for (int i = 0; i < stockInfoInternals.size(); i++) {
            StockInfoInternal stockInfoInternal = stockInfoInternals.get(i);
            StockRequest stockRequest = stockRequests.get(i);
            int value = stockInfoInternal.getLatestPrice().multiply(stockRequest.getVolume()).intValue();
            sum += value;
        }
        return sum;
    }
}
