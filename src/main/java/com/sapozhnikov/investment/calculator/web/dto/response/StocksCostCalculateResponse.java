package com.sapozhnikov.investment.calculator.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksCostCalculateResponse {
    private BigDecimal value;
    private List<AllocationResponse> allocations;
}
