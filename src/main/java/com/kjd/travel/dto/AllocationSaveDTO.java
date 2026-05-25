package com.kjd.travel.dto;

import java.math.BigDecimal;

public record AllocationSaveDTO(
        Long companyId,
        Long projectId,
        BigDecimal allocationRatio,
        BigDecimal allocationAmount
) {
}
