package com.sapozhnikov.investment.calculator.web.controllers;

import com.sapozhnikov.investment.calculator.services.StockService;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.sapozhnikov.investment.calculator.TestCommons.readStringFromFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@RunWith(SpringRunner.class)
public class StocksControllerTest {

    @MockBean
    private StockService stockService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCalculateCostStocks() throws Exception {
        List<AllocationResponse> allocations = new ArrayList<>();
        allocations.add(
                new AllocationResponse("Technology", new BigDecimal(600000), new BigDecimal(0.375)));
        allocations.add(
                new AllocationResponse("Healthcare", new BigDecimal(1000000), new BigDecimal(0.625)));
        BigDecimal value = new BigDecimal(1600000);
        given(stockService.calculateCost(any()))
                .willReturn(new StocksCostCalculateResponse(value, allocations));
        var requestBody = readStringFromFile("/web/request/requestCalculateCostStocks.json");
        var expectedResponseBody = readStringFromFile("/web/response/responseCalculateCostStocks.json");

        mockMvc.perform(post("/v1/stocks/cost/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void testCalculateCostStocksEmptyStocks() throws Exception {
        given(stockService.calculateCost(any()))
                .willReturn(new StocksCostCalculateResponse(BigDecimal.ZERO, new ArrayList<>()));
        var requestBody = readStringFromFile("/web/request/requestEmptyCalculateCostStocks.json");
        var expectedResponseBody = readStringFromFile("/web/response/responseEmptyCalculateCostStocks.json");

        mockMvc.perform(post("/v1/stocks/cost/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void testCalculateCostStocksBadRequest() throws Exception {
        var requestBody = readStringFromFile("/web/request/requestNotValidCalculateCostStocks.json");
        mockMvc.perform(post("/v1/stocks/cost/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
