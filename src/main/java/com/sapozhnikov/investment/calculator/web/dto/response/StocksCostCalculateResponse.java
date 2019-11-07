package com.sapozhnikov.investment.calculator.web.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class StocksCostCalculateResponse {
    private final BigDecimal value;
    private final List<AllocationResponse> allocations;
}
