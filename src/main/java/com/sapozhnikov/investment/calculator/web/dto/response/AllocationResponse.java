package com.sapozhnikov.investment.calculator.web.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class AllocationResponse {
    private final String sector;
    private final BigDecimal assetValue;
    private final BigDecimal proportion;
}
