package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithLatestPrice;
import com.sapozhnikov.investment.calculator.services.dto.financial.StockInfoExternal;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithSector;
import com.sapozhnikov.investment.calculator.utils.exceptions.internal.InternalException;
import com.sapozhnikov.investment.calculator.utils.exceptions.internal.InternalServerInternalException;
import com.sapozhnikov.investment.calculator.utils.exceptions.internal.NotFoundInternalException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class FinancialService {

    private static final String SERVICE_IS_TEMPORARILY_UNAVAILABLE = "service is temporarily unavailable";

    private final String iexApiToken;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

    public FinancialService(@Value("${iex.api.token}") String iexApiToken,
                            @Value("${iex.thread.quantity}") int threadQuantity,
                            @Qualifier("externalSystemRestTemplate") RestTemplate restTemplate) {
        this.iexApiToken = iexApiToken;
        this.restTemplate = restTemplate;
        this.executorService = Executors.newFixedThreadPool(threadQuantity);
    }

    public List<SymbolWithLatestPrice> getLatestPrice(Collection<String> stockSymbols) {
        List<SymbolWithLatestPrice> listSymbolWithLatestPrice = new CopyOnWriteArrayList<>();
        List<Callable<SymbolWithLatestPrice>> tasksGetLatestPrice = stockSymbols.stream()
                .map(symbol -> createCallableForGetLatestPrice(listSymbolWithLatestPrice, symbol))
                .collect(Collectors.toList());
        runAndWaitAllTask(tasksGetLatestPrice);
        return listSymbolWithLatestPrice;
    }

    //TODO ADD cache
    public BigDecimal getLatestPrice(String stockSymbol) {
        try {
            return restTemplate.getForObject(
                    "https://cloud.iexapis.com/stable/stock/{stockSymbol}/quote/latestPrice?token={iexApiToken}",
                    BigDecimal.class, stockSymbol, iexApiToken);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound e) {
            throw new NotFoundInternalException(e.getResponseBodyAsString() + " " + stockSymbol);
        } catch (Exception e) {
            throw new InternalServerInternalException(SERVICE_IS_TEMPORARILY_UNAVAILABLE);
        }
    }

    public List<SymbolWithSector> getSector(Collection<String> stockSymbols) {
        List<SymbolWithSector> listSymbolWithSector = new CopyOnWriteArrayList<>();
        List<Callable<SymbolWithSector>> tasksGetLatestPrice = stockSymbols.stream()
                .map(symbol -> createCallableForGetSector(listSymbolWithSector, symbol))
                .collect(Collectors.toList());
        runAndWaitAllTask(tasksGetLatestPrice);
        return listSymbolWithSector;
    }

    //TODO ADD cache
    public StockInfoExternal getSector(String stockSymbol) {
        try {
            return restTemplate.getForObject(
                    "https://cloud.iexapis.com/stable/stock/{stockSymbol}/company?token={iexApiToken}",
                    StockInfoExternal.class, stockSymbol, iexApiToken);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound e) {
            throw new NotFoundInternalException(e.getResponseBodyAsString() + " " + stockSymbol);
        } catch (Exception e) {
            throw new InternalServerInternalException(SERVICE_IS_TEMPORARILY_UNAVAILABLE);
        }
    }

    private Callable<SymbolWithLatestPrice> createCallableForGetLatestPrice(
            List<SymbolWithLatestPrice> listSymbolWithLatestPrice, String symbol) {
        return () -> {
            BigDecimal latestPrice = getLatestPrice(symbol);
            SymbolWithLatestPrice symbolWithLatestPrice =
                    new SymbolWithLatestPrice(symbol, latestPrice);
            listSymbolWithLatestPrice.add(symbolWithLatestPrice);
            return symbolWithLatestPrice;
        };
    }

    private Callable<SymbolWithSector> createCallableForGetSector(
            List<SymbolWithSector> listSymbolWithSector, String symbol) {
        return () -> {
            StockInfoExternal stockInfoExternal = getSector(symbol);
            String sector = stockInfoExternal.getSector();
            SymbolWithSector symbolWithSector = new SymbolWithSector(symbol, sector);
            listSymbolWithSector.add(symbolWithSector);
            return symbolWithSector;
        };
    }

    private <T> void runAndWaitAllTask(List<Callable<T>> tasks) {
        try {
            List<Future<T>> futures = executorService.invokeAll(tasks);
            for (Future<T> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InternalException) {
                throw (InternalException) cause;
            } else {
                throw new InternalServerInternalException(SERVICE_IS_TEMPORARILY_UNAVAILABLE);
            }
        }
    }
}
