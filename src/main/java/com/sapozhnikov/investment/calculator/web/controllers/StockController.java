package com.sapozhnikov.investment.calculator.web.controllers;

import com.sapozhnikov.investment.calculator.services.StockService;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stocks")
public class StockController {

    private final StockService stockService;

    @PostMapping("/cost/calculate")
    public StocksCostCalculateResponse calculateCostStocks(
            @Valid @RequestBody StocksCostCalculateRequest stocksCostCalculateRequest) {
        return stockService.calculateCost(stocksCostCalculateRequest);
    }
}
