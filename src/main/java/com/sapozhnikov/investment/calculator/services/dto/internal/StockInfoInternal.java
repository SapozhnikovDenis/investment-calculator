package com.sapozhnikov.investment.calculator.services.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockInfoInternal {
    private String symbol;
    private String sector;
    private BigDecimal latestPrice;
}
