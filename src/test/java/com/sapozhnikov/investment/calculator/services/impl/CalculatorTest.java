package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.Calculator;
import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Calculator calculator = new Calculator();

    @Test
    void testCalculateProportion() {
        BigDecimal partValue = BigDecimal.valueOf(99.987);

        BigDecimal proportion = calculator.calculateProportion(BigDecimal.valueOf(100), partValue);

        assertThat(proportion, is(partValue));
    }

    @Test
    void testCalculateSum() {
        BigDecimal bigDecimal = BigDecimal.valueOf(33.987654321);
        BigDecimal expectedSum = bigDecimal
                .multiply(BigDecimal.valueOf(3))
                .setScale(3, RoundingMode.HALF_DOWN);

        BigDecimal actualSum = calculator.calculateSum(Arrays.asList(bigDecimal, bigDecimal, bigDecimal));

        assertThat(actualSum, is(expectedSum));
    }

}