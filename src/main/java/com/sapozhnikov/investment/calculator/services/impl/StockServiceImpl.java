package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.StockService;
import com.sapozhnikov.investment.calculator.web.dto.request.StocksCostCalculateRequest;
import com.sapozhnikov.investment.calculator.web.dto.response.StocksCostCalculateResponse;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {

    //TODO add realization
    @Override
    public StocksCostCalculateResponse calculateCost(StocksCostCalculateRequest stocksCostCalculateRequest) {
        throw new UnsupportedOperationException();
    }
}
