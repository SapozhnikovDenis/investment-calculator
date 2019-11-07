package com.sapozhnikov.investment.calculator.services.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SymbolWithSector {
    private final String symbol;
    private final String sector;
}
