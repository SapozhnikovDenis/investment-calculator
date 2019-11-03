package com.sapozhnikov.investment.calculator.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.sapozhnikov.investment.calculator.TestCommons.readStringFromFile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StocksCostCalculateResponseParsingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testParsing() throws JsonProcessingException {
        var expectedJson = readStringFromFile("/web/response/responseCalculateCostStocks.json");
        List<AllocationResponse> allocations = new ArrayList<>();
        allocations.add(
                new AllocationResponse("Technology", new BigDecimal(600000), new BigDecimal(0.375)));
        allocations.add(
                new AllocationResponse("Healthcare", new BigDecimal(1000000), new BigDecimal(0.625)));
        BigDecimal value = new BigDecimal(1600000);
        StocksCostCalculateResponse stocksCostCalculateResponse = new StocksCostCalculateResponse(value, allocations);

        String actualJson = objectMapper.writeValueAsString(stocksCostCalculateResponse);

        assertThat(actualJson, is(expectedJson));
    }

    @Test
    public void testParsingEmpty() throws JsonProcessingException {
        var expectedJson = readStringFromFile("/web/response/responseEmptyCalculateCostStocks.json");
        List<AllocationResponse> allocations = new ArrayList<>();
        BigDecimal value = new BigDecimal(0);
        StocksCostCalculateResponse stocksCostCalculateResponse = new StocksCostCalculateResponse(value, allocations);

        String actualJson = objectMapper.writeValueAsString(stocksCostCalculateResponse);

        assertThat(actualJson, is(expectedJson));
    }
}
