package com.kjd.travel.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReimbursementDetailVO(
        Long id,
        String reimNo,
        Integer status,
        String statusName,
        String reimbursementTitle,
        Long reimburserId,
        String reimburserNo,
        String reimburserName,
        Long reimDepartmentId,
        String reimDepartmentNo,
        String reimDepartmentName,
        Long reimCompanyId,
        String reimCompanyNo,
        String reimCompanyName,
        Long businessTypeId,
        String businessTypeNo,
        String businessTypeName,
        String businessTripReason,
        BigDecimal subsidyTotal,
        BigDecimal mealAllowance,
        BigDecimal transportationAllowance,
        BigDecimal phoneAllowance,
        String remarks,
        Integer version,
        LocalDateTime creationTime,
        LocalDateTime updateTime,
        List<TripVO> trips,
        List<AllocationVO> allocations
) {
}
