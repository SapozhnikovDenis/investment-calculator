package com.sapozhnikov.investment.calculator.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import static java.math.BigDecimal.ZERO;

@Service
public class Calculator {

    private static final int SCALE_FOR_PROPORTION = 3;
    private static final double ONE_HUNDRED = 100;

    public BigDecimal calculateSum(Collection<BigDecimal> numbers) {
        return sutTrailingZerosAndScale(numbers.stream()
                .reduce(BigDecimal::add)
                .orElse(ZERO));
    }

    public BigDecimal calculateProportion(BigDecimal fullValue, BigDecimal partValue) {
        double proportion = partValue.doubleValue() * ONE_HUNDRED / fullValue.doubleValue();
        return sutTrailingZerosAndScale(new BigDecimal(proportion));
    }

    private BigDecimal sutTrailingZerosAndScale(BigDecimal value) {
        return new BigDecimal(value.setScale(SCALE_FOR_PROPORTION, RoundingMode.HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString());
    }
}
