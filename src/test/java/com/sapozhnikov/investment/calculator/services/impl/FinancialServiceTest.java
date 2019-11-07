package com.sapozhnikov.investment.calculator.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.dto.financial.StockInfoExternal;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithLatestPrice;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithSector;
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
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
class FinancialServiceTest {

    private final String token = "test";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private FinancialService financialService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
        financialService = new FinancialService(token, 10, restTemplate);
    }

    @Test
    void testGetLatestPrice() throws Exception {
        String symbol = "AAPL";
        BigDecimal expectedLatestPrice = TEN;
        initMockLatestPrice(symbol, expectedLatestPrice);

        BigDecimal actualLatestPrice = financialService.getLatestPrice(symbol);

        assertThat(actualLatestPrice, is(expectedLatestPrice));
    }

    @Test
    void testGetLatestPriceList() throws Exception {
        String firstSymbol = "AAPL";
        BigDecimal expectedFirstLatestPrice = TEN;
        initMockLatestPrice(firstSymbol, expectedFirstLatestPrice);
        String secondSymbol = "TWTR";
        BigDecimal expectedSecondLatestPrice = ONE;
        initMockLatestPrice(secondSymbol, expectedSecondLatestPrice);

        List<SymbolWithLatestPrice> listLatestPrice =
                financialService.getLatestPrice(Arrays.asList(firstSymbol, secondSymbol));

        assertThat(listLatestPrice.size(), is(2));
        SymbolWithLatestPrice firstSymbolWithLatestPrice = listLatestPrice.stream()
                .filter(symbolWithLatestPrice -> symbolWithLatestPrice.getSymbol().equals(firstSymbol))
                .findFirst()
                .get();
        assertThat(firstSymbolWithLatestPrice.getSymbol(), is(firstSymbol));
        assertThat(firstSymbolWithLatestPrice.getLatestPrice(), is(expectedFirstLatestPrice));
        SymbolWithLatestPrice secondSymbolWithLatestPrice = listLatestPrice.stream()
                .filter(symbolWithLatestPrice -> symbolWithLatestPrice.getSymbol().equals(secondSymbol))
                .findFirst()
                .get();
        assertThat(secondSymbolWithLatestPrice.getSymbol(), is(secondSymbol));
        assertThat(secondSymbolWithLatestPrice.getLatestPrice(), is(expectedSecondLatestPrice));
    }

    @Test
    void testGetSector() throws Exception {
        String symbol = "AAPL";
        String expectedSector = "Technology";
        initMockSector(symbol, expectedSector);

        StockInfoExternal actualSector = financialService.getSector(symbol);

        assertThat(actualSector.getSector(), is(expectedSector));
    }

    @Test
    void testGetSectorList() throws Exception {
        String firstSymbol = "AAPL";
        String firstSector = "Technology";
        initMockSector(firstSymbol, firstSector);
        String secondSymbol = "TWTR";
        String secondSector = "Health Technology";
        initMockSector(secondSymbol, secondSector);

        List<SymbolWithSector> listSymbolWithSector =
                financialService.getSector(Arrays.asList(firstSymbol, secondSymbol));

        assertThat(listSymbolWithSector.size(), is(2));
        SymbolWithSector firstSymbolWithSector = listSymbolWithSector.stream()
                .filter(symbolWithSector -> symbolWithSector.getSymbol().equals(firstSymbol))
                .findFirst()
                .get();
        assertThat(firstSymbolWithSector.getSymbol(), is(firstSymbol));
        assertThat(firstSymbolWithSector.getSector(), is(firstSector));
        SymbolWithSector secondSymbolWithSector = listSymbolWithSector.stream()
                .filter(symbolWithSector -> symbolWithSector.getSymbol().equals(secondSymbol))
                .findFirst()
                .get();
        assertThat(secondSymbolWithSector.getSymbol(), is(secondSymbol));
        assertThat(secondSymbolWithSector.getSector(), is(secondSector));
    }

    private void initMockLatestPrice(String symbol, BigDecimal latestPrice) throws Exception {
        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(new URI("https://cloud.iexapis.com/stable/stock/" + symbol + "/quote/latestPrice?token=" + token)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(latestPrice))
                );
    }

    private void initMockSector(String symbol, String sector) throws Exception {
        StockInfoExternal stockInfoExternal = new StockInfoExternal(symbol, sector);
        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(new URI("https://cloud.iexapis.com/stable/stock/" + symbol + "/company?token=" + token)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(stockInfoExternal))
                );
    }
}