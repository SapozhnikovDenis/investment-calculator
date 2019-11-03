package com.sapozhnikov.investment.calculator.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.MessageFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@RunWith(SpringRunner.class)
public class StocksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCalculateCostStocks() throws Exception {
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


    private String readStringFromFile(String path) {
        try {
            return new String(getClass().getResourceAsStream(path).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("Error reading file {0}", path));
        }
    }
}
