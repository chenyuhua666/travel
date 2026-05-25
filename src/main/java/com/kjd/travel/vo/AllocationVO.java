package com.kjd.travel.vo;

import java.math.BigDecimal;

public record AllocationVO(
        Long id,
        Long companyId,
        String companyNo,
        String companyName,
        Long projectId,
        String projectNo,
        String projectName,
        BigDecimal allocationRatio,
        BigDecimal allocationAmount,
        Integer rowOrder
) {
}
