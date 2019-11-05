package com.sapozhnikov.investment.calculator.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.dto.financial.StockInfoExternal;
import com.sapozhnikov.investment.calculator.services.dto.internal.StockInfoInternal;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;

import static java.math.BigDecimal.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
class FinancialServiceImplTest {

    private final String token = "test";
    private final ObjectMapper objectMapper = new ObjectMapper();


    private FinancialService financialService;
    private MockRestServiceServer mockServer;


    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        financialService = new FinancialServiceImpl(token, 10, restTemplate);
    }

    @Test
    void testGetStockInfo() throws Exception {
        String symbol = "AAPL";
        String sector = "Technology";
        BigDecimal latestPrice = TEN;
        initMock(symbol, sector, latestPrice);

        StockInfoInternal stockInfo = financialService.getStockInfo(symbol);

        assertThat(stockInfo.getLatestPrice(), is(latestPrice));
        assertThat(stockInfo.getSector(), is(sector));
        assertThat(stockInfo.getSymbol(), is(symbol));
    }

    @Test
    void testGetStockInfoList() throws Exception {
        String symbol = "AAPL";
        String sector = "Technology";
        BigDecimal latestPrice = TEN;
        initMock(symbol, sector, latestPrice);
        StockRequest firstStockRequest = new StockRequest(symbol, ZERO);
        StockRequest secondStockRequest = new StockRequest(symbol, ONE);
        HashSet<StockRequest> stockRequests = new HashSet<>();
        stockRequests.add(firstStockRequest);
        stockRequests.add(secondStockRequest);

        Map<StockRequest, StockInfoInternal> financialServiceStockInfo =
                financialService.getStockInfo(stockRequests);

        assertThat(financialServiceStockInfo.size(), is(2));
        StockInfoInternal firstStockInfo = financialServiceStockInfo.get(firstStockRequest);
        assertThat(firstStockInfo.getLatestPrice(), is(latestPrice));
        assertThat(firstStockInfo.getSector(), is(sector));
        assertThat(firstStockInfo.getSymbol(), is(symbol));
        StockInfoInternal secondStockInfo = financialServiceStockInfo.get(secondStockRequest);
        assertThat(secondStockInfo.getLatestPrice(), is(latestPrice));
        assertThat(secondStockInfo.getSector(), is(sector));
        assertThat(secondStockInfo.getSymbol(), is(symbol));
    }

    private void initMock(String symbol, String sector, BigDecimal latestPrice) throws Exception {
        StockInfoExternal stockInfoExternal = new StockInfoExternal(symbol, sector);
        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(new URI("https://cloud.iexapis.com/stable/stock/" + symbol + "/company?token=" + token)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(stockInfoExternal))
                );
        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(new URI("https://cloud.iexapis.com/stable/stock/" + symbol + "/quote/latestPrice?token=" + token)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(latestPrice))
                );
    }
}