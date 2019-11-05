package com.sapozhnikov.investment.calculator.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AllocationResponse {
    private String sector;
    private BigDecimal assetValue;
    private BigDecimal proportion;
}
