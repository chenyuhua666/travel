package com.kjd.travel.controller;

import com.kjd.travel.vo.PageResult;
import com.kjd.travel.vo.Result;
import com.kjd.travel.config.security.CurrentUser;
import com.kjd.travel.dto.ReimbursementDraftSaveDTO;
import com.kjd.travel.service.ReimbursementService;
import com.kjd.travel.vo.ReimbursementActionVO;
import com.kjd.travel.vo.ReimbursementDetailVO;
import com.kjd.travel.vo.ReimbursementListVO;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reimbursements")
public class ReimbursementController {

    private final ReimbursementService reimbursementService;

    public ReimbursementController(ReimbursementService reimbursementService) {
        this.reimbursementService = reimbursementService;
    }

    @GetMapping
    public Result<PageResult<ReimbursementListVO>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String reimNo,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long reimburserId,
            @RequestParam(required = false) Long businessTypeId,
            @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.page(current, size, reimNo, title, reason, companyId,
                departmentId, reimburserId, businessTypeId, user));
    }

    @GetMapping("/{id}")
    public Result<ReimbursementDetailVO> detail(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.detail(id, user));
    }

    @PostMapping("/drafts")
    public Result<ReimbursementDetailVO> createDraft(@Valid @RequestBody ReimbursementDraftSaveDTO dto,
                                                     @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.createDraft(dto, user));
    }

    @PutMapping("/{id}/draft")
    public Result<ReimbursementDetailVO> saveDraft(@PathVariable Long id,
                                                   @Valid @RequestBody ReimbursementDraftSaveDTO dto,
                                                   @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.saveDraft(id, dto, user));
    }

    @PostMapping("/{id}/submit")
    public Result<ReimbursementActionVO> submit(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.submit(id, user));
    }

    @PostMapping("/{id}/withdraw")
    public Result<ReimbursementActionVO> withdraw(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.withdraw(id, user));
    }

    @PostMapping("/{id}/approve")
    public Result<ReimbursementActionVO> approve(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.approve(id, user));
    }

    @PostMapping("/{id}/void")
    public Result<ReimbursementActionVO> voidByOwner(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.voidByOwner(id, user));
    }

    @PostMapping("/{id}/copy")
    public Result<ReimbursementDetailVO> copy(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        return Result.success(reimbursementService.copy(id, user));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDraft(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user) {
        reimbursementService.deleteDraft(id, user);
        return Result.success();
    }
}
