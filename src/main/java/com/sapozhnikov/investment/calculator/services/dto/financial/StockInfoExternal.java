package com.sapozhnikov.investment.calculator.services.dto.financial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInfoExternal {
    private String symbol;
    private String sector;
}
