package com.sapozhnikov.investment.calculator.services.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SymbolWithAssetValue {
    private final String symbol;
    private final BigDecimal assetValue;
}
