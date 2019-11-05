package com.sapozhnikov.investment.calculator.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    @NotEmpty
    private String symbol;
    @NotNull
    private BigDecimal volume;
}
