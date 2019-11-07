package com.sapozhnikov.investment.calculator.services;

import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.SectorWithAssetValue;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithLatestPrice;
import com.sapozhnikov.investment.calculator.services.dto.internal.SymbolWithSector;
import com.sapozhnikov.investment.calculator.web.dto.request.StockRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockMatcher {

    public Map<SymbolWithLatestPrice, BigDecimal> matchLatestPriceWithVolumeBySymbol(
            List<SymbolWithLatestPrice> listSymbolWithLatestPrice, List<StockRequest> stocks) {
        Map<SymbolWithLatestPrice, BigDecimal> symbolWithLatestPriceMatchVolume = new HashMap<>();
        Map<String, BigDecimal> symbolVersusVolume = stocks.stream()
                .collect(Collectors.toMap(
                        StockRequest::getSymbol, StockRequest::getVolume));
        listSymbolWithLatestPrice.forEach(symbolWithLatestPrice -> {
            String symbol = symbolWithLatestPrice.getSymbol();
            BigDecimal volume = symbolVersusVolume.get(symbol);
            symbolWithLatestPriceMatchVolume.put(symbolWithLatestPrice, volume);
        });
        return symbolWithLatestPriceMatchVolume;
    }

    public List<SectorWithAssetValue> matchSectorWithAssetValueBySymbol(
            List<SymbolWithSector> listSymbolWithSector, List<SymbolWithAssetValue> listSymbolWithAssetValue) {
        List<SectorWithAssetValue> listSectorWithAssetValue = new ArrayList<>();
        Map<String, BigDecimal> symbolVersusAssetValue = listSymbolWithAssetValue.stream()
                .collect(Collectors.toMap(
                        SymbolWithAssetValue::getSymbol, SymbolWithAssetValue::getAssetValue));
        listSymbolWithSector.forEach(symbolWithSector -> {
            String symbol = symbolWithSector.getSymbol();
            String sector = symbolWithSector.getSector();
            BigDecimal assetValue = symbolVersusAssetValue.get(symbol);
            SectorWithAssetValue sectorWithAssetValue = new SectorWithAssetValue(sector, assetValue);
            listSectorWithAssetValue.add(sectorWithAssetValue);
        });
        return listSectorWithAssetValue;
    }

    public List<SectorWithAssetValue> matchSectorWithAssetValueBySectors(
            List<SectorWithAssetValue> listSectorWithAssetValue) {
        Map<String, SectorWithAssetValue> matchedSectorWithAssetValue = new HashMap<>();
        listSectorWithAssetValue.forEach(newSectorWithAssetValue -> {
            String sector = newSectorWithAssetValue.getSector();
            BigDecimal assertValue = newSectorWithAssetValue.getAssetValue();
            SectorWithAssetValue sectorWithAssetValue;
            if (matchedSectorWithAssetValue.containsKey(sector)) {
                sectorWithAssetValue = matchedSectorWithAssetValue.get(sector);
                assertValue = sectorWithAssetValue.getAssetValue().add(assertValue);
            }
            sectorWithAssetValue = new SectorWithAssetValue(sector, assertValue);
            matchedSectorWithAssetValue.put(sector, sectorWithAssetValue);
        });
        return new ArrayList<>(matchedSectorWithAssetValue.values());
    }
}
