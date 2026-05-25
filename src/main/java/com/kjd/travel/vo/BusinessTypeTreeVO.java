package com.kjd.travel.vo;

import java.util.List;

public record BusinessTypeTreeVO(
        Long id,
        String businessTypeNo,
        String businessTypeName,
        Long parentId,
        boolean leaf,
        List<BusinessTypeTreeVO> children
) {
}
