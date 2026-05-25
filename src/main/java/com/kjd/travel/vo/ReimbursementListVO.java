package com.kjd.travel.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReimbursementListVO(
        Long id,
        String reimNo,
        Integer status,
        String statusName,
        String reimburserDisplay,
        String departmentDisplay,
        String reimCompanyName,
        String businessTypeName,
        String reimbursementTitle,
        String businessTripReason,
        BigDecimal subsidyTotal,
        LocalDateTime creationTime
) {
}
