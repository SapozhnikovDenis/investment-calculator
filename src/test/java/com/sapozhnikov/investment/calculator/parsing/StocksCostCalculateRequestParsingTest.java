package com.sapozhnikov.investment.calculator.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static com.sapozhnikov.investment.calculator.TestCommons.readStringFromFile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StocksCostCalculateRequestParsingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testParsing() throws JsonProcessingException {
        var requestJson = readStringFromFile("/web/request/requestCalculateCostStocks.json");
        var expectedStocksCostCalculateRequest = buildExpected();

        StocksCostCalculateRequest actualStocksCostCalculateRequest =
                objectMapper.readValue(requestJson, StocksCostCalculateRequest.class);

        assertThat(actualStocksCostCalculateRequest, is(expectedStocksCostCalculateRequest));
    }

    @Test
    public void testParsingEmpty() throws JsonProcessingException {
        var requestJson = readStringFromFile("/web/request/requestEmptyCalculateCostStocks.json");
        var expectedStocksCostCalculateRequest = new StocksCostCalculateRequest(Collections.emptyList());

        StocksCostCalculateRequest actualStocksCostCalculateRequest =
                objectMapper.readValue(requestJson, StocksCostCalculateRequest.class);

        assertThat(actualStocksCostCalculateRequest, is(expectedStocksCostCalculateRequest));
    }

    @Test(expected = InvalidFormatException.class)
    public void testParsingInvalidJson() throws JsonProcessingException {
        var requestJson = readStringFromFile("/web/request/requestNotValidCalculateCostStocks.json");

        objectMapper.readValue(requestJson, StocksCostCalculateRequest.class);
    }

    private StocksCostCalculateRequest buildExpected() {
        StocksCostCalculateRequest stocksCostCalculateRequest = new StocksCostCalculateRequest();
        ArrayList<StockRequest> stocks = new ArrayList<>();
        stocks.add(new StockRequest("AAPL", new BigDecimal(50)));
        stocks.add(new StockRequest("HOG", new BigDecimal(10)));
        stocks.add(new StockRequest("MDSO", new BigDecimal(1)));
        stocks.add(new StockRequest("IDRA", new BigDecimal(1)));
        stocks.add(new StockRequest("MRSN", new BigDecimal(1)));
        stocksCostCalculateRequest.setStocks(stocks);
        return stocksCostCalculateRequest;
    }
}
