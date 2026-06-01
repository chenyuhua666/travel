package com.kjd.travel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReimbursementDraftSaveDTO(
        @Size(max = 500, message = "报销标题不能超过500字") String reimbursementTitle,
        Long reimburserId,
        Long reimDepartmentId,
        Long reimCompanyId,
        Long businessTypeId,
        @Size(max = 500, message = "出差事由不能超过500字") String businessTripReason,
        @Size(max = 1000, message = "备注不能超过1000字") String remarks,
        Integer version,
        List<@Valid TripSaveDTO> trips,
        List<AllocationSaveDTO> allocations
) {
}
