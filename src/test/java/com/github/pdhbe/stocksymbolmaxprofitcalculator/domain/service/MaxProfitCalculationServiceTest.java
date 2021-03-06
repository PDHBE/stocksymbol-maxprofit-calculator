package com.github.pdhbe.stocksymbolmaxprofitcalculator.domain.service;

import com.github.pdhbe.stocksymbolmaxprofitcalculator.domain.model.StockDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
class MaxProfitCalculationServiceTest {
    @Autowired
    MaxProfitCalculationService maxProfitCalculationService;

    @Test
    void validStockList() {
        List<StockDto> stockDtoList = new ArrayList<>();
        stockDtoList.add(
                StockDto.builder()
                        .date(LocalDate.now().minusDays(2))
                        .lowPrice(1)
                        .highPrice(10)
                        .build()
        );
        stockDtoList.add(
                StockDto.builder()
                        .date(LocalDate.now().minusDays(1))
                        .lowPrice(3)
                        .highPrice(8)
                        .build()
        );
        stockDtoList.add(
                StockDto.builder()
                        .date(LocalDate.now())
                        .lowPrice(2)
                        .highPrice(9)
                        .build()
        );
        MaxProfitDto expectedMaxProfit = MaxProfitDto.builder()
                .stockSymbol("validSymbol")
                .buyDate(LocalDate.now().minusDays(2))
                .sellDate(LocalDate.now())
                .maxProfit(8.0)
                .build();

        MaxProfitDto actualMaxProfit = maxProfitCalculationService.calculate("validSymbol", stockDtoList);

        assertEquals(expectedMaxProfit, actualMaxProfit);
    }

    @Test
    void lessThanTwoDays() {
        List<StockDto> stockDtoList = new ArrayList<>();
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now())
                .lowPrice(0)
                .highPrice(10)
                .build());

        assertThrows(LessThanTwoDaysException.class, () -> {
            maxProfitCalculationService.calculate("validSymbol", stockDtoList);
        });
    }

    @Test
    void allPricesSame() {
        List<StockDto> stockDtoList = new ArrayList<>();
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now().minusDays(2))
                .lowPrice(10)
                .highPrice(10)
                .build());
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now().minusDays(1))
                .lowPrice(10)
                .highPrice(10)
                .build());
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now())
                .lowPrice(10)
                .highPrice(10)
                .build());


        assertThrows(ZeroProfitException.class, () -> {
            maxProfitCalculationService.calculate("validSymbol", stockDtoList);
        });
    }

    @Test
    void pricesKeepFalling() {
        List<StockDto> stockDtoList = new ArrayList<>();
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now().minusDays(2))
                .lowPrice(12)
                .highPrice(12)
                .build());
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now().minusDays(1))
                .lowPrice(11)
                .highPrice(11)
                .build());
        stockDtoList.add(StockDto.builder()
                .date(LocalDate.now())
                .lowPrice(10)
                .highPrice(10)
                .build());

        assertThrows(MinusProfitException.class, () -> {
            maxProfitCalculationService.calculate("validSymbol", stockDtoList);
        });
    }
}