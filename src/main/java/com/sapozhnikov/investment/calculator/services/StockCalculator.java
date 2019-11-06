package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.web.dto.response.AllocationResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockCalculator {

    BigDecimal calculateLatestPrice(Optional<AllocationResponse> optionalAllocationResponse,
                                    BigDecimal volume, BigDecimal latestPrice);

    BigDecimal calculateSum(List<AllocationResponse> allocationResponses);

    List<AllocationResponse> enrichProportion(List<AllocationResponse> allocationResponses, BigDecimal sum);
}
