package com.sapozhnikov.investment.calculator.services.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StockInfoInternal {
    private final String symbol;
    private final String sector;
    private final BigDecimal latestPrice;
}
