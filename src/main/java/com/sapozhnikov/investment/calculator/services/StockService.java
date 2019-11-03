package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;

public interface StockService {

    StocksCostCalculateResponse calculateCost(StocksCostCalculateRequest stocksCostCalculateRequest);

}
