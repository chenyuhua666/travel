package com.kjd.travel.service;

import com.kjd.travel.config.exception.BusinessException;
import com.kjd.travel.config.security.CurrentUser;
import com.kjd.travel.dto.AllocationSaveDTO;
import com.kjd.travel.dto.LoginDTO;
import com.kjd.travel.dto.ReimbursementDraftSaveDTO;
import com.kjd.travel.dto.SubsidyDaySaveDTO;
import com.kjd.travel.dto.TripSaveDTO;
import com.kjd.travel.vo.ReimbursementActionVO;
import com.kjd.travel.vo.ReimbursementDetailVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReimbursementServiceTests {

    private static final CurrentUser DEMO_USER = new CurrentUser(1L, 1L, "demo", Set.of("EMPLOYEE"));

    @Autowired
    private AuthService authService;

    @Autowired
    private ReimbursementService reimbursementService;

    @Test
    void loginDraftSubmitApproveWithdrawAndVoidFlowWorks() {
        assertThat(authService.login(new LoginDTO("demo", "Travel@123")).token()).isNotBlank();

        ReimbursementDetailVO draft = reimbursementService.createDraft(completeDraft(), DEMO_USER);

        assertThat(draft.reimNo()).startsWith("SY");
        assertThat(draft.status()).isZero();
        assertThat(draft.subsidyTotal()).isEqualByComparingTo("180.00");
        assertThat(draft.trips()).hasSize(1);
        assertThat(draft.trips().get(0).subsidy().days()).hasSize(1);

        ReimbursementActionVO submitted = reimbursementService.submit(draft.id(), DEMO_USER);
        assertThat(submitted.status()).isEqualTo(3);

        ReimbursementActionVO withdrawn = reimbursementService.withdraw(draft.id(), DEMO_USER);
        assertThat(withdrawn.status()).isZero();

        ReimbursementActionVO resubmitted = reimbursementService.submit(draft.id(), DEMO_USER);
        assertThat(resubmitted.status()).isEqualTo(3);

        ReimbursementActionVO approved = reimbursementService.approve(draft.id(), DEMO_USER);
        assertThat(approved.status()).isEqualTo(1);

        ReimbursementActionVO voided = reimbursementService.voidByOwner(draft.id(), DEMO_USER);
        assertThat(voided.status()).isEqualTo(2);
    }

    @Test
    void overlappingTripsForSameTravelerAreRejected() {
        TripSaveDTO firstTrip = trip(LocalDate.of(2026, 5, 19), LocalDate.of(2026, 5, 20));
        TripSaveDTO secondTrip = trip(LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 21));
        ReimbursementDraftSaveDTO draft = new ReimbursementDraftSaveDTO("overlap", 1L, 1L, 3L, 3L,
                "validate overlap", null, null, List.of(firstTrip, secondTrip), List.of());

        assertThatThrownBy(() -> reimbursementService.createDraft(draft, DEMO_USER))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void staleDraftVersionIsRejected() {
        ReimbursementDetailVO draft = reimbursementService.createDraft(completeDraft(), DEMO_USER);

        ReimbursementDetailVO saved = reimbursementService.saveDraft(draft.id(), completeDraft(draft.version()), DEMO_USER);

        assertThat(saved.version()).isGreaterThan(draft.version());
        assertThatThrownBy(() -> reimbursementService.saveDraft(draft.id(), completeDraft(draft.version()), DEMO_USER))
                .isInstanceOf(BusinessException.class)
                .satisfies(error -> assertThat(((BusinessException) error).getCode()).isEqualTo(409));
    }

    private ReimbursementDraftSaveDTO completeDraft() {
        return completeDraft(null);
    }

    private ReimbursementDraftSaveDTO completeDraft(Integer version) {
        TripSaveDTO trip = trip(LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 20));
        AllocationSaveDTO allocation = new AllocationSaveDTO(3L, 1L, BigDecimal.ONE, new BigDecimal("180.00"));
        return new ReimbursementDraftSaveDTO("Beijing project trip", 1L, 1L, 3L, 3L,
                "customer project communication", "test remarks", version, List.of(trip), List.of(allocation));
    }

    private TripSaveDTO trip(LocalDate departDate, LocalDate arriveDate) {
        SubsidyDaySaveDTO day = new SubsidyDaySaveDTO(departDate, true, new BigDecimal("100.00"),
                true, new BigDecimal("40.00"), true, new BigDecimal("40.00"));
        return new TripSaveDTO(1L, 3L, 1L, departDate, arriveDate, "Wuhan to Beijing", List.of(day));
    }
}
