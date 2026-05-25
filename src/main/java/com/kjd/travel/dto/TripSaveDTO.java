package com.kjd.travel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record TripSaveDTO(
        Long travelerId,
        Long departCityId,
        Long arriveCityId,
        LocalDate departDate,
        LocalDate arriveDate,
        @Size(max = 500, message = "行程说明不能超过500字") String tripDescription,
        List<@Valid SubsidyDaySaveDTO> subsidyDays
) {
}
