package com.kjd.travel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubsidyDaySaveDTO(
        LocalDate subsidyDate,
        Boolean mealSelected,
        BigDecimal mealAmount,
        Boolean transportationSelected,
        BigDecimal transportationAmount,
        Boolean phoneSelected,
        BigDecimal phoneAmount
) {
}
