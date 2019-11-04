package com.sapozhnikov.investment.calculator.services.impl;

import com.sapozhnikov.investment.calculator.services.FinancialService;
import com.sapozhnikov.investment.calculator.services.dto.financial.StockInfoExternal;
import com.sapozhnikov.investment.calculator.services.dto.internal.StockInfoInternal;
import com.sapozhnikov.investment.calculator.utils.exceptions.internal.InternalServerInternalException;
import com.sapozhnikov.investment.calculator.utils.exceptions.internal.NotFoundInternalException;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class FinancialServiceImpl implements FinancialService {

    private final String iexApiToken;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

    public FinancialServiceImpl(@Value("${iex.api.token}") String iexApiToken,
                                @Value("${iex.thread.quantity}") int threadQuantity,
                                RestTemplate restTemplate) {
        this.iexApiToken = iexApiToken;
        this.restTemplate = restTemplate;
        this.executorService = Executors.newFixedThreadPool(threadQuantity);
    }

    //TODO add async
    @Override
    public StockInfoInternal getStockInfo(String stockSymbol) {
        try {
            StockInfoExternal stockInfoExternal = restTemplate.getForObject(
                    "https://cloud.iexapis.com/stable/stock/{stockSymbol}/company?token={iexApiToken}",
                    StockInfoExternal.class, stockSymbol, iexApiToken);
            BigDecimal latestPrice = restTemplate.getForObject(
                    "https://cloud.iexapis.com/stable/stock/{stockSymbol}/quote/latestPrice?token={iexApiToken}",
                    BigDecimal.class, stockSymbol, iexApiToken);
            return mapToStockInfoInternal(stockInfoExternal, latestPrice);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound e) {
            throw new NotFoundInternalException(e.getResponseBodyAsString() + " " + stockSymbol);
        } catch (Exception e) {
            throw new InternalServerInternalException("service is temporarily unavailable");
        }
    }

    @SneakyThrows
    @Override
    public Map<StockRequest, StockInfoInternal> getStockInfo(Set<StockRequest> stockRequests) {
        Map<StockRequest, StockInfoInternal> concurrentHashMap = new ConcurrentHashMap<>();
        stockRequests.forEach(stockRequest ->
                executorService.submit(() -> {
                    String symbol = stockRequest.getSymbol();
                    StockInfoInternal stockInfo = getStockInfo(symbol);
                    concurrentHashMap.put(stockRequest, stockInfo);
                })
        );
        while (concurrentHashMap.size() != stockRequests.size()) {
            TimeUnit.MILLISECONDS.sleep(10);
        }
        return new HashMap<>(concurrentHashMap);
    }

    private StockInfoInternal mapToStockInfoInternal(StockInfoExternal stockInfoExternal, BigDecimal latestPrice) {
        return new StockInfoInternal(stockInfoExternal.getSymbol(), stockInfoExternal.getSector(), latestPrice);
    }

}
