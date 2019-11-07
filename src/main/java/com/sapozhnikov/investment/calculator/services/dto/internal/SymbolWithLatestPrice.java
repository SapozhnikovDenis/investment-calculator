package com.sapozhnikov.investment.calculator.services.dto.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class SymbolWithLatestPrice {
    private final String symbol;
    private final BigDecimal latestPrice;
}
