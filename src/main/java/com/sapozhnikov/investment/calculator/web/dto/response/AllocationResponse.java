package com.sapozhnikov.investment.calculator.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocationResponse {
    private String sector;
    private BigDecimal assetValue;
    private BigDecimal proportion;
}
