package com.kjd.travel.vo;

import java.math.BigDecimal;
import java.util.List;

public record SubsidyVO(
        Long id,
        Integer subsidyDays,
        BigDecimal applyAmount,
        BigDecimal subsidyAmount,
        BigDecimal mealAmount,
        BigDecimal transportationAmount,
        BigDecimal phoneAmount,
        List<SubsidyDayVO> days
) {
}
