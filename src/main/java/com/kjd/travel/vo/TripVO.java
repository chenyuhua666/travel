package com.kjd.travel.vo;

import java.time.LocalDate;

public record TripVO(
        Long id,
        Long travelerId,
        String travelerNo,
        String travelerName,
        Long departCityId,
        String departCityName,
        Long arriveCityId,
        String arriveCityName,
        LocalDate departDate,
        LocalDate arriveDate,
        String tripDescription,
        SubsidyVO subsidy
) {
}
