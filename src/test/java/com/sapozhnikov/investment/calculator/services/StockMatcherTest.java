package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.services.dto.internal.SectorWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithLatestPrice;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithSector;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.*;
import static java.math.BigDecimal.ONE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StockMatcherTest {

    private final StockMatcher stockMatcher = new StockMatcher();

    private final BigDecimal sum = TEN;
    private final String symbol = "A";
    private final String sector = "B";
    private final BigDecimal proportion = valueOf(100);
    private final BigDecimal volume = ONE;
    private final BigDecimal latestPrice = ZERO;
    private final BigDecimal assetValue = valueOf(500);

    @Test
    void testMatchLatestPriceWithVolumeBySymbolEmptyLists() {
        Map<SymbolWithLatestPrice, BigDecimal> symbolWithLatestPriceBigDecimalMap =
                stockMatcher.matchLatestPriceWithVolumeBySymbol(Collections.emptyList(), Collections.emptyList());

        assertThat(symbolWithLatestPriceBigDecimalMap.isEmpty(), is(true));
    }
    @Test
    void testMatchLatestPriceWithVolumeBySymbol() {
        SymbolWithLatestPrice symbolWithLatestPrice = new SymbolWithLatestPrice(symbol, latestPrice);
        List<SymbolWithLatestPrice> listSymbolWithLatestPrice =
                Collections.singletonList(symbolWithLatestPrice);
        List<StockRequest> stocks = Collections.singletonList(new StockRequest(symbol, volume));

        Map<SymbolWithLatestPrice, BigDecimal> matchLatestPriceWithVolumeBySymbol =
                stockMatcher.matchLatestPriceWithVolumeBySymbol(listSymbolWithLatestPrice, stocks);

        assertThat(matchLatestPriceWithVolumeBySymbol.size(), is(1));
        assertThat(matchLatestPriceWithVolumeBySymbol.get(symbolWithLatestPrice), is(volume));
    }

    @Test
    void testMatchSectorWithAssetValueBySymbolEmptyLists() {
        List<SectorWithAssetValue> sectorWithAssetValues =
                stockMatcher.matchSectorWithAssetValueBySymbol(Collections.emptyList(), Collections.emptyList());

        assertThat(sectorWithAssetValues.isEmpty(), is(true));
    }

    @Test
    void testMatchSectorWithAssetValueBySymbol() {
        List<SymbolWithSector> listSymbolWithSector =
                Collections.singletonList(new SymbolWithSector(symbol, sector));
        List<SymbolWithAssetValue> listSymbolWithAssetValue =
                Collections.singletonList(new SymbolWithAssetValue(symbol, assetValue));

        List<SectorWithAssetValue> sectorWithAssetValues =
                stockMatcher.matchSectorWithAssetValueBySymbol(listSymbolWithSector, listSymbolWithAssetValue);

        SectorWithAssetValue sectorWithAssetValue = sectorWithAssetValues.get(0);
        assertThat(sectorWithAssetValues.size(), is(1));
        assertThat(sectorWithAssetValue.getAssetValue(), is(assetValue));
        assertThat(sectorWithAssetValue.getSector(), is(sector));
    }

    @Test
    void matchSectorWithAssetValueBySectorsEmptyLists() {
        List<SectorWithAssetValue> matchSectorWithAssetValueBySectors = stockMatcher.matchSectorWithAssetValueBySectors(
                Collections.emptyList());

        assertThat(matchSectorWithAssetValueBySectors.isEmpty(), is(true));
    }

    @Test
    void matchSectorWithAssetValueBySectors() {
        BigDecimal secondAssetValue = valueOf(123);
        BigDecimal firstAssetValue = valueOf(321);
        List<SectorWithAssetValue> listSectorWithAssetValue = Arrays.asList(
                new SectorWithAssetValue(sector, firstAssetValue),
                new SectorWithAssetValue(sector, secondAssetValue));

        List<SectorWithAssetValue> matchSectorWithAssetValueBySectors = stockMatcher.matchSectorWithAssetValueBySectors(
                listSectorWithAssetValue);

        SectorWithAssetValue sectorWithAssetValue = matchSectorWithAssetValueBySectors.get(0);
        assertThat(matchSectorWithAssetValueBySectors.size(), is(1));
        assertThat(sectorWithAssetValue.getSector(), is(sector));
        assertThat(sectorWithAssetValue.getAssetValue(), is(firstAssetValue.add(secondAssetValue)));
    }
}