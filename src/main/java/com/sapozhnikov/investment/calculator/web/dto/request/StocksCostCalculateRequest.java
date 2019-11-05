package com.sapozhnikov.investment.calculator.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksCostCalculateRequest {
    @Valid
    private List<StockRequest> stocks;
}
