package com.kjd.travel.entity;

import com.kjd.travel.config.exception.BusinessException;

public enum ReimbursementStatus {
    DRAFT(0, "草稿"),
    APPROVED(1, "审批通过"),
    VOIDED(2, "已作废"),
    APPROVING(3, "审批中");

    private final int code;
    private final String label;

    ReimbursementStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ReimbursementStatus fromCode(Integer code) {
        for (ReimbursementStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new BusinessException("未知报销单状态");
    }
}
