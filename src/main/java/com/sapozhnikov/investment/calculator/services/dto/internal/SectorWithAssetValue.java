package com.sapozhnikov.investment.calculator.services.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SectorWithAssetValue {
    private final String sector;
    private final BigDecimal assetValue;
}
