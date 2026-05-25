package com.kjd.travel.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubsidyDayVO(
        Long id,
        LocalDate subsidyDate,
        String weekdayName,
        Long cityId,
        String cityName,
        BigDecimal mealStandardAmount,
        BigDecimal transportationStandardAmount,
        BigDecimal phoneStandardAmount,
        boolean mealSelected,
        boolean transportationSelected,
        boolean phoneSelected,
        BigDecimal mealAmount,
        BigDecimal transportationAmount,
        BigDecimal phoneAmount
) {
}
