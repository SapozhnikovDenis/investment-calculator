package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.services.dto.internal.StockInfoInternal;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;

import java.util.Map;
import java.util.Set;

public interface FinancialService {

    StockInfoInternal getStockInfo(String stockSymbol);

    Map<StockRequest, StockInfoInternal> getStockInfo(Set<StockRequest> stockRequests);
}
