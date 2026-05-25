package com.kjd.travel.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjd.travel.config.exception.BusinessException;
import com.kjd.travel.vo.PageResult;
import com.kjd.travel.config.security.CurrentUser;
import com.kjd.travel.entity.BusinessTypeEntity;
import com.kjd.travel.entity.CityEntity;
import com.kjd.travel.entity.CompanyEntity;
import com.kjd.travel.entity.DepartmentEntity;
import com.kjd.travel.entity.EmployeeEntity;
import com.kjd.travel.entity.ProjectEntity;
import com.kjd.travel.mapper.BusinessTypeMapper;
import com.kjd.travel.mapper.CityMapper;
import com.kjd.travel.mapper.CompanyMapper;
import com.kjd.travel.mapper.DepartmentMapper;
import com.kjd.travel.mapper.EmployeeMapper;
import com.kjd.travel.mapper.ProjectMapper;
import com.kjd.travel.dto.AllocationSaveDTO;
import com.kjd.travel.dto.ReimbursementDraftSaveDTO;
import com.kjd.travel.dto.SubsidyDaySaveDTO;
import com.kjd.travel.dto.TripSaveDTO;
import com.kjd.travel.entity.AllocationEntity;
import com.kjd.travel.entity.ReimbursementEntity;
import com.kjd.travel.entity.SubsidyDayEntity;
import com.kjd.travel.entity.SubsidyEntity;
import com.kjd.travel.entity.TripEntity;
import com.kjd.travel.entity.ReimbursementStatus;
import com.kjd.travel.mapper.AllocationMapper;
import com.kjd.travel.mapper.ReimbursementMapper;
import com.kjd.travel.mapper.SubsidyDayMapper;
import com.kjd.travel.mapper.SubsidyMapper;
import com.kjd.travel.mapper.TripMapper;
import com.kjd.travel.vo.AllocationVO;
import com.kjd.travel.vo.ReimbursementActionVO;
import com.kjd.travel.vo.ReimbursementDetailVO;
import com.kjd.travel.vo.ReimbursementListVO;
import com.kjd.travel.vo.SubsidyDayVO;
import com.kjd.travel.vo.SubsidyVO;
import com.kjd.travel.vo.TripVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class ReimbursementService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal TRANSPORTATION_STANDARD = amount("40");
    private static final BigDecimal PHONE_STANDARD = amount("40");

    private final ReimbursementMapper reimbursementMapper;
    private final TripMapper tripMapper;
    private final SubsidyMapper subsidyMapper;
    private final SubsidyDayMapper subsidyDayMapper;
    private final AllocationMapper allocationMapper;
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final CompanyMapper companyMapper;
    private final BusinessTypeMapper businessTypeMapper;
    private final CityMapper cityMapper;
    private final ProjectMapper projectMapper;

    public ReimbursementService(ReimbursementMapper reimbursementMapper, TripMapper tripMapper, SubsidyMapper subsidyMapper,
                                SubsidyDayMapper subsidyDayMapper, AllocationMapper allocationMapper,
                                EmployeeMapper employeeMapper, DepartmentMapper departmentMapper,
                                CompanyMapper companyMapper, BusinessTypeMapper businessTypeMapper,
                                CityMapper cityMapper, ProjectMapper projectMapper) {
        this.reimbursementMapper = reimbursementMapper;
        this.tripMapper = tripMapper;
        this.subsidyMapper = subsidyMapper;
        this.subsidyDayMapper = subsidyDayMapper;
        this.allocationMapper = allocationMapper;
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
        this.companyMapper = companyMapper;
        this.businessTypeMapper = businessTypeMapper;
        this.cityMapper = cityMapper;
        this.projectMapper = projectMapper;
    }

    public PageResult<ReimbursementListVO> page(long current, long size, String reimNo, String title, String reason,
                                                Long companyId, Long departmentId, Long reimburserId,
                                                Long businessTypeId, CurrentUser user) {
        Page<ReimbursementEntity> page = reimbursementMapper.selectPage(
                Page.of(Math.max(current, 1), Math.min(Math.max(size, 1), 100)),
                Wrappers.<ReimbursementEntity>lambdaQuery()
                        .like(StringUtils.hasText(reimNo), ReimbursementEntity::getReimNo, reimNo)
                        .like(StringUtils.hasText(title), ReimbursementEntity::getReimbursementTitle, title)
                        .like(StringUtils.hasText(reason), ReimbursementEntity::getBusinessTripReason, reason)
                        .eq(companyId != null, ReimbursementEntity::getReimCompanyId, companyId)
                        .eq(departmentId != null, ReimbursementEntity::getReimDepartmentId, departmentId)
                        .eq(reimburserId != null, ReimbursementEntity::getReimburserId, reimburserId)
                        .eq(businessTypeId != null, ReimbursementEntity::getBusinessTypeId, businessTypeId)
                        .eq(!isAdmin(user), ReimbursementEntity::getOwnerUserId, user.userId())
                        .orderByDesc(ReimbursementEntity::getCreationTime)
        );
        List<ReimbursementListVO> records = page.getRecords().stream().map(this::toListVO).toList();
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    public ReimbursementDetailVO detail(Long id, CurrentUser user) {
        ReimbursementEntity main = requireVisible(id, user);
        return buildDetail(main);
    }

    @Transactional
    public ReimbursementDetailVO createDraft(ReimbursementDraftSaveDTO dto, CurrentUser user) {
        return saveDraft(null, dto, user);
    }

    @Transactional
    public ReimbursementDetailVO saveDraft(Long id, ReimbursementDraftSaveDTO dto, CurrentUser user) {
        ReimbursementEntity main = id == null ? newDraft(user) : requireEditable(id, user);
        fillMain(main, dto);
        main.setUpdateTime(LocalDateTime.now());
        if (main.getId() == null) {
            reimbursementMapper.insert(main);
            main.setReimNo("SY" + String.format("%010d", main.getId()));
            reimbursementMapper.updateById(main);
        } else {
            reimbursementMapper.updateById(main);
        }
        replaceChildren(main, dto);
        return buildDetail(reimbursementMapper.selectById(main.getId()));
    }

    @Transactional
    public ReimbursementActionVO submit(Long id, CurrentUser user) {
        ReimbursementEntity main = requireEditable(id, user);
        validateForSubmit(main);
        main.setStatus(ReimbursementStatus.APPROVING.getCode());
        main.setUpdateTime(LocalDateTime.now());
        reimbursementMapper.updateById(main);
        return toActionVO(main);
    }

    @Transactional
    public ReimbursementActionVO withdraw(Long id, CurrentUser user) {
        ReimbursementEntity main = requireOwned(id, user);
        if (!Objects.equals(main.getStatus(), ReimbursementStatus.APPROVING.getCode())) {
            throw new BusinessException("仅审批中的报销单可撤回");
        }
        main.setStatus(ReimbursementStatus.DRAFT.getCode());
        main.setUpdateTime(LocalDateTime.now());
        reimbursementMapper.updateById(main);
        return toActionVO(main);
    }

    @Transactional
    public ReimbursementActionVO approve(Long id, CurrentUser user) {
        ReimbursementEntity main = requireOwned(id, user);
        if (!Objects.equals(main.getStatus(), ReimbursementStatus.APPROVING.getCode())) {
            throw new BusinessException("仅审批中的报销单可通过");
        }
        main.setStatus(ReimbursementStatus.APPROVED.getCode());
        main.setUpdateTime(LocalDateTime.now());
        reimbursementMapper.updateById(main);
        return toActionVO(main);
    }

    @Transactional
    public ReimbursementActionVO voidByOwner(Long id, CurrentUser user) {
        ReimbursementEntity main = requireOwned(id, user);
        if (Objects.equals(main.getStatus(), ReimbursementStatus.VOIDED.getCode())) {
            throw new BusinessException("报销单已作废");
        }
        main.setStatus(ReimbursementStatus.VOIDED.getCode());
        main.setUpdateTime(LocalDateTime.now());
        reimbursementMapper.updateById(main);
        return toActionVO(main);
    }

    @Transactional
    public ReimbursementDetailVO copy(Long id, CurrentUser user) {
        ReimbursementDetailVO source = detail(id, user);
        List<TripSaveDTO> trips = source.trips().stream()
                .map(trip -> new TripSaveDTO(trip.travelerId(), trip.departCityId(), trip.arriveCityId(),
                        trip.departDate(), trip.arriveDate(), trip.tripDescription(),
                        trip.subsidy() == null ? List.of() : trip.subsidy().days().stream()
                                .map(day -> new SubsidyDaySaveDTO(day.subsidyDate(), day.mealSelected(), day.mealAmount(),
                                        day.transportationSelected(), day.transportationAmount(), day.phoneSelected(),
                                        day.phoneAmount()))
                                .toList()))
                .toList();
        List<AllocationSaveDTO> allocations = source.allocations().stream()
                .map(allocation -> new AllocationSaveDTO(allocation.companyId(), allocation.projectId(),
                        allocation.allocationRatio(), allocation.allocationAmount()))
                .toList();
        ReimbursementDraftSaveDTO dto = new ReimbursementDraftSaveDTO(source.reimbursementTitle(),
                source.reimburserId(), source.reimDepartmentId(), source.reimCompanyId(), source.businessTypeId(),
                source.businessTripReason(), source.remarks(), trips, allocations);
        return createDraft(dto, user);
    }

    @Transactional
    public void deleteDraft(Long id, CurrentUser user) {
        ReimbursementEntity main = requireOwned(id, user);
        if (!Objects.equals(main.getStatus(), ReimbursementStatus.DRAFT.getCode())) {
            throw new BusinessException("仅草稿报销单可删除");
        }
        reimbursementMapper.deleteById(main.getId());
    }

    private ReimbursementEntity newDraft(CurrentUser user) {
        ReimbursementEntity entity = new ReimbursementEntity();
        entity.setOwnerUserId(user.userId());
        entity.setStatus(ReimbursementStatus.DRAFT.getCode());
        entity.setCreationTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        setMainTotals(entity, ZERO, ZERO, ZERO, ZERO);
        return entity;
    }

    private void fillMain(ReimbursementEntity main, ReimbursementDraftSaveDTO dto) {
        main.setReimbursementTitle(trimToNull(dto.reimbursementTitle()));
        main.setBusinessTripReason(trimToNull(dto.businessTripReason()));
        main.setRemarks(trimToNull(dto.remarks()));
        fillReimburser(main, dto.reimburserId());
        fillDepartment(main, dto.reimDepartmentId());
        fillCompany(main, dto.reimCompanyId());
        fillBusinessType(main, dto.businessTypeId());
    }

    private void fillReimburser(ReimbursementEntity main, Long employeeId) {
        if (employeeId == null) {
            main.setReimburserId(null);
            main.setReimburserNo(null);
            main.setReimburserName(null);
            return;
        }
        EmployeeEntity employee = requireEmployee(employeeId);
        main.setReimburserId(employee.getId());
        main.setReimburserNo(employee.getEmployeeNo());
        main.setReimburserName(employee.getEmployeeName());
    }

    private void fillDepartment(ReimbursementEntity main, Long departmentId) {
        if (departmentId == null) {
            main.setReimDepartmentId(null);
            main.setReimDepartmentNo(null);
            main.setReimDepartmentName(null);
            return;
        }
        DepartmentEntity department = requireDepartment(departmentId);
        main.setReimDepartmentId(department.getId());
        main.setReimDepartmentNo(department.getDepartmentNo());
        main.setReimDepartmentName(department.getDepartmentName());
    }

    private void fillCompany(ReimbursementEntity main, Long companyId) {
        if (companyId == null) {
            main.setReimCompanyId(null);
            main.setReimCompanyNo(null);
            main.setReimCompanyName(null);
            return;
        }
        CompanyEntity company = requireCompany(companyId);
        main.setReimCompanyId(company.getId());
        main.setReimCompanyNo(company.getCompanyNo());
        main.setReimCompanyName(company.getCompanyName());
    }

    private void fillBusinessType(ReimbursementEntity main, Long businessTypeId) {
        if (businessTypeId == null) {
            main.setBusinessTypeId(null);
            main.setBusinessTypeNo(null);
            main.setBusinessTypeName(null);
            return;
        }
        BusinessTypeEntity businessType = requireBusinessType(businessTypeId);
        main.setBusinessTypeId(businessType.getId());
        main.setBusinessTypeNo(businessType.getBusinessTypeNo());
        main.setBusinessTypeName(businessType.getBusinessTypeName());
    }

    private void replaceChildren(ReimbursementEntity main, ReimbursementDraftSaveDTO dto) {
        deleteChildren(main.getId());
        List<TripSaveDTO> trips = safe(dto.trips());
        validateTripRanges(trips);
        Summary summary = new Summary();
        for (TripSaveDTO tripDTO : trips) {
            TripEntity trip = saveTrip(main.getId(), tripDTO);
            SubsidyEntity subsidy = saveSubsidy(main.getId(), trip, tripDTO);
            summary.add(subsidy);
        }
        saveAllocations(main.getId(), safe(dto.allocations()));
        setMainTotals(main, summary.subsidyTotal, summary.mealTotal, summary.transportationTotal, summary.phoneTotal);
        main.setUpdateTime(LocalDateTime.now());
        reimbursementMapper.updateById(main);
    }

    private void deleteChildren(Long mainId) {
        List<SubsidyEntity> subsidies = subsidyMapper.selectList(Wrappers.<SubsidyEntity>lambdaQuery()
                .eq(SubsidyEntity::getMainId, mainId));
        List<Long> subsidyIds = subsidies.stream().map(SubsidyEntity::getId).toList();
        if (!subsidyIds.isEmpty()) {
            subsidyDayMapper.delete(Wrappers.<SubsidyDayEntity>lambdaQuery().in(SubsidyDayEntity::getSubsidyId, subsidyIds));
        }
        subsidyMapper.delete(Wrappers.<SubsidyEntity>lambdaQuery().eq(SubsidyEntity::getMainId, mainId));
        tripMapper.delete(Wrappers.<TripEntity>lambdaQuery().eq(TripEntity::getMainId, mainId));
        allocationMapper.delete(Wrappers.<AllocationEntity>lambdaQuery().eq(AllocationEntity::getMainId, mainId));
    }

    private TripEntity saveTrip(Long mainId, TripSaveDTO dto) {
        requireTripField(dto.travelerId(), "出行人");
        requireTripField(dto.departCityId(), "出发城市");
        requireTripField(dto.arriveCityId(), "到达城市");
        requireTripField(dto.departDate(), "出发日期");
        requireTripField(dto.arriveDate(), "到达日期");
        if (!StringUtils.hasText(dto.tripDescription())) {
            throw new BusinessException("行程说明不能为空");
        }
        EmployeeEntity traveler = requireEmployee(dto.travelerId());
        CityEntity departCity = requireCity(dto.departCityId());
        CityEntity arriveCity = requireCity(dto.arriveCityId());
        TripEntity trip = new TripEntity();
        trip.setMainId(mainId);
        trip.setTravelerId(traveler.getId());
        trip.setTravelerNo(traveler.getEmployeeNo());
        trip.setTravelerName(traveler.getEmployeeName());
        trip.setDepartCityId(departCity.getId());
        trip.setDepartCityNo(departCity.getCityNo());
        trip.setDepartCityName(departCity.getCityName());
        trip.setArriveCityId(arriveCity.getId());
        trip.setArriveCityNo(arriveCity.getCityNo());
        trip.setArriveCityName(arriveCity.getCityName());
        trip.setDepartDate(dto.departDate());
        trip.setArriveDate(dto.arriveDate());
        trip.setTripDescription(dto.tripDescription().trim());
        tripMapper.insert(trip);
        return trip;
    }

    private SubsidyEntity saveSubsidy(Long mainId, TripEntity trip, TripSaveDTO dto) {
        CityEntity subsidyCity = requireCity(trip.getArriveCityId());
        Map<LocalDate, SubsidyDaySaveDTO> selectionByDate = toSelectionMap(dto.subsidyDays(), trip);
        SubsidyEntity subsidy = new SubsidyEntity();
        subsidy.setMainId(mainId);
        subsidy.setTripId(trip.getId());
        subsidy.setSubsidyDays((int) (trip.getDepartDate().datesUntil(trip.getArriveDate().plusDays(1)).count()));
        subsidy.setApplyAmount(ZERO);
        subsidy.setSubsidyAmount(ZERO);
        subsidy.setMealAmount(ZERO);
        subsidy.setTransportationAmount(ZERO);
        subsidy.setPhoneAmount(ZERO);
        subsidyMapper.insert(subsidy);

        BigDecimal mealStandard = mealStandard(subsidyCity.getCityType());
        for (LocalDate date = trip.getDepartDate(); !date.isAfter(trip.getArriveDate()); date = date.plusDays(1)) {
            SubsidyDaySaveDTO selection = selectionByDate.get(date);
            boolean mealSelected = selected(selection == null ? null : selection.mealSelected());
            boolean transportationSelected = selected(selection == null ? null : selection.transportationSelected());
            boolean phoneSelected = selected(selection == null ? null : selection.phoneSelected());
            BigDecimal mealAmount = resolveSubsidyAmount(mealSelected, selection == null ? null : selection.mealAmount(),
                    mealStandard, "餐费补助");
            BigDecimal transportationAmount = resolveSubsidyAmount(transportationSelected,
                    selection == null ? null : selection.transportationAmount(), TRANSPORTATION_STANDARD, "交通补助");
            BigDecimal phoneAmount = resolveSubsidyAmount(phoneSelected, selection == null ? null : selection.phoneAmount(),
                    PHONE_STANDARD, "通讯补助");
            SubsidyDayEntity day = subsidyDay(subsidy, subsidyCity, date, mealStandard, mealSelected,
                    transportationSelected, phoneSelected, mealAmount, transportationAmount, phoneAmount);
            subsidyDayMapper.insert(day);
            subsidy.setApplyAmount(add(subsidy.getApplyAmount(),
                    selectedStandard(mealSelected, mealStandard),
                    selectedStandard(transportationSelected, TRANSPORTATION_STANDARD),
                    selectedStandard(phoneSelected, PHONE_STANDARD)));
            subsidy.setMealAmount(add(subsidy.getMealAmount(), mealAmount));
            subsidy.setTransportationAmount(add(subsidy.getTransportationAmount(), transportationAmount));
            subsidy.setPhoneAmount(add(subsidy.getPhoneAmount(), phoneAmount));
        }
        subsidy.setSubsidyAmount(add(subsidy.getMealAmount(), subsidy.getTransportationAmount(), subsidy.getPhoneAmount()));
        subsidyMapper.updateById(subsidy);
        return subsidy;
    }

    private Map<LocalDate, SubsidyDaySaveDTO> toSelectionMap(List<SubsidyDaySaveDTO> days, TripEntity trip) {
        Map<LocalDate, SubsidyDaySaveDTO> selectionByDate = new HashMap<>();
        for (SubsidyDaySaveDTO day : safe(days)) {
            if (day.subsidyDate() == null) {
                throw new BusinessException("补助日期不能为空");
            }
            if (day.subsidyDate().isBefore(trip.getDepartDate()) || day.subsidyDate().isAfter(trip.getArriveDate())) {
                throw new BusinessException("补助日期不在行程范围内");
            }
            if (selectionByDate.put(day.subsidyDate(), day) != null) {
                throw new BusinessException("同一行程的补助日期不可重复");
            }
        }
        return selectionByDate;
    }

    private SubsidyDayEntity subsidyDay(SubsidyEntity subsidy, CityEntity city, LocalDate date, BigDecimal mealStandard,
                                        boolean mealSelected, boolean transportationSelected, boolean phoneSelected,
                                        BigDecimal mealAmount, BigDecimal transportationAmount, BigDecimal phoneAmount) {
        SubsidyDayEntity day = new SubsidyDayEntity();
        day.setSubsidyId(subsidy.getId());
        day.setSubsidyDate(date);
        day.setWeekdayName(weekdayName(date.getDayOfWeek()));
        day.setCityId(city.getId());
        day.setCityName(city.getCityName());
        day.setMealStandardAmount(mealStandard);
        day.setTransportationStandardAmount(TRANSPORTATION_STANDARD);
        day.setPhoneStandardAmount(PHONE_STANDARD);
        day.setMealSelected(flag(mealSelected));
        day.setTransportationSelected(flag(transportationSelected));
        day.setPhoneSelected(flag(phoneSelected));
        day.setMealAmount(mealAmount);
        day.setTransportationAmount(transportationAmount);
        day.setPhoneAmount(phoneAmount);
        return day;
    }

    private void saveAllocations(Long mainId, List<AllocationSaveDTO> allocations) {
        int order = 1;
        for (AllocationSaveDTO dto : allocations) {
            if (dto.companyId() == null) {
                continue;
            }
            CompanyEntity company = requireCompany(dto.companyId());
            ProjectEntity project = dto.projectId() == null ? null : requireProject(dto.projectId());
            AllocationEntity allocation = new AllocationEntity();
            allocation.setMainId(mainId);
            allocation.setCompanyId(company.getId());
            allocation.setCompanyNo(company.getCompanyNo());
            allocation.setCompanyName(company.getCompanyName());
            if (project != null) {
                allocation.setProjectId(project.getId());
                allocation.setProjectNo(project.getProjectNo());
                allocation.setProjectName(project.getProjectName());
            }
            allocation.setAllocationRatio(normalizeRatio(dto.allocationRatio()));
            allocation.setAllocationAmount(normalizeAmount(dto.allocationAmount()));
            allocation.setRowOrder(order++);
            allocationMapper.insert(allocation);
        }
    }

    private void validateTripRanges(List<TripSaveDTO> trips) {
        for (int index = 0; index < trips.size(); index++) {
            TripSaveDTO trip = trips.get(index);
            if (trip.departDate() == null || trip.arriveDate() == null) {
                continue;
            }
            if (trip.arriveDate().isBefore(trip.departDate())) {
                throw new BusinessException("到达日期不可早于出发日期");
            }
            if (trip.arriveDate().isAfter(LocalDate.now())) {
                throw new BusinessException("到达日期不可晚于当前日期");
            }
            for (int next = index + 1; next < trips.size(); next++) {
                TripSaveDTO another = trips.get(next);
                if (trip.travelerId() != null && trip.travelerId().equals(another.travelerId())
                        && trip.arriveDate() != null && trip.departDate() != null
                        && another.arriveDate() != null && another.departDate() != null
                        && !trip.arriveDate().isBefore(another.departDate())
                        && !another.arriveDate().isBefore(trip.departDate())) {
                    throw new BusinessException("同一出行人的行程日期不可重复");
                }
            }
        }
    }

    private void validateForSubmit(ReimbursementEntity main) {
        requireMainField(main.getReimbursementTitle(), "报销标题");
        requireMainField(main.getReimburserId(), "报销人");
        requireMainField(main.getReimDepartmentId(), "报销部门");
        requireMainField(main.getReimCompanyId(), "费用归属公司");
        requireMainField(main.getBusinessTypeId(), "业务类型");
        requireMainField(main.getBusinessTripReason(), "出差事由");
        List<TripEntity> trips = tripMapper.selectList(Wrappers.<TripEntity>lambdaQuery()
                .eq(TripEntity::getMainId, main.getId()));
        if (trips.isEmpty()) {
            throw new BusinessException("至少需要一条补录行程");
        }
        validatePersistedTripRanges(trips);
        List<AllocationEntity> allocations = allocationMapper.selectList(Wrappers.<AllocationEntity>lambdaQuery()
                .eq(AllocationEntity::getMainId, main.getId()));
        if (allocations.isEmpty()) {
            throw new BusinessException("至少需要一条分摊信息");
        }
        BigDecimal ratioTotal = allocations.stream()
                .map(AllocationEntity::getAllocationRatio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ratioTotal.compareTo(BigDecimal.ONE) != 0) {
            throw new BusinessException("分摊比例合计必须为100%");
        }
        BigDecimal amountTotal = allocations.stream()
                .map(AllocationEntity::getAllocationAmount)
                .reduce(ZERO, ReimbursementService::add);
        if (amountTotal.compareTo(normalizeAmount(main.getSubsidyTotal())) != 0) {
            throw new BusinessException("分摊金额合计必须等于补助总金额");
        }
    }

    private void validatePersistedTripRanges(List<TripEntity> trips) {
        List<TripEntity> ordered = trips.stream()
                .sorted(Comparator.comparing(TripEntity::getTravelerId).thenComparing(TripEntity::getDepartDate))
                .toList();
        for (int index = 1; index < ordered.size(); index++) {
            TripEntity previous = ordered.get(index - 1);
            TripEntity current = ordered.get(index);
            if (previous.getTravelerId().equals(current.getTravelerId())
                    && !previous.getArriveDate().isBefore(current.getDepartDate())) {
                throw new BusinessException("同一出行人的行程日期不可重复");
            }
        }
    }

    private ReimbursementDetailVO buildDetail(ReimbursementEntity main) {
        List<TripEntity> trips = tripMapper.selectList(Wrappers.<TripEntity>lambdaQuery()
                .eq(TripEntity::getMainId, main.getId()).orderByAsc(TripEntity::getId));
        List<SubsidyEntity> subsidies = subsidyMapper.selectList(Wrappers.<SubsidyEntity>lambdaQuery()
                .eq(SubsidyEntity::getMainId, main.getId()));
        Map<Long, SubsidyEntity> subsidyByTripId = new HashMap<>();
        subsidies.forEach(subsidy -> subsidyByTripId.put(subsidy.getTripId(), subsidy));
        List<Long> subsidyIds = subsidies.stream().map(SubsidyEntity::getId).toList();
        Map<Long, List<SubsidyDayEntity>> daysBySubsidyId = new HashMap<>();
        if (!subsidyIds.isEmpty()) {
            subsidyDayMapper.selectList(Wrappers.<SubsidyDayEntity>lambdaQuery()
                            .in(SubsidyDayEntity::getSubsidyId, subsidyIds)
                            .orderByAsc(SubsidyDayEntity::getSubsidyDate))
                    .forEach(day -> daysBySubsidyId.computeIfAbsent(day.getSubsidyId(), ignored -> new ArrayList<>()).add(day));
        }
        List<TripVO> tripVOs = trips.stream()
                .map(trip -> toTripVO(trip, subsidyByTripId.get(trip.getId()), daysBySubsidyId))
                .toList();
        List<AllocationVO> allocationVOs = allocationMapper.selectList(Wrappers.<AllocationEntity>lambdaQuery()
                        .eq(AllocationEntity::getMainId, main.getId())
                        .orderByAsc(AllocationEntity::getRowOrder))
                .stream()
                .map(this::toAllocationVO)
                .toList();
        ReimbursementStatus status = ReimbursementStatus.fromCode(main.getStatus());
        return new ReimbursementDetailVO(main.getId(), main.getReimNo(), status.getCode(), status.getLabel(),
                main.getReimbursementTitle(), main.getReimburserId(), main.getReimburserNo(), main.getReimburserName(),
                main.getReimDepartmentId(), main.getReimDepartmentNo(), main.getReimDepartmentName(),
                main.getReimCompanyId(), main.getReimCompanyNo(), main.getReimCompanyName(),
                main.getBusinessTypeId(), main.getBusinessTypeNo(), main.getBusinessTypeName(),
                main.getBusinessTripReason(), main.getSubsidyTotal(), main.getMealAllowance(),
                main.getTransportationAllowance(), main.getPhoneAllowance(), main.getRemarks(), main.getCreationTime(),
                main.getUpdateTime(), tripVOs, allocationVOs);
    }

    private TripVO toTripVO(TripEntity trip, SubsidyEntity subsidy, Map<Long, List<SubsidyDayEntity>> daysBySubsidyId) {
        SubsidyVO subsidyVO = null;
        if (subsidy != null) {
            List<SubsidyDayVO> days = daysBySubsidyId.getOrDefault(subsidy.getId(), List.of()).stream()
                    .map(this::toSubsidyDayVO)
                    .toList();
            subsidyVO = new SubsidyVO(subsidy.getId(), subsidy.getSubsidyDays(), subsidy.getApplyAmount(),
                    subsidy.getSubsidyAmount(), subsidy.getMealAmount(), subsidy.getTransportationAmount(),
                    subsidy.getPhoneAmount(), days);
        }
        return new TripVO(trip.getId(), trip.getTravelerId(), trip.getTravelerNo(), trip.getTravelerName(),
                trip.getDepartCityId(), trip.getDepartCityName(), trip.getArriveCityId(), trip.getArriveCityName(),
                trip.getDepartDate(), trip.getArriveDate(), trip.getTripDescription(), subsidyVO);
    }

    private SubsidyDayVO toSubsidyDayVO(SubsidyDayEntity day) {
        return new SubsidyDayVO(day.getId(), day.getSubsidyDate(), day.getWeekdayName(), day.getCityId(),
                day.getCityName(), day.getMealStandardAmount(), day.getTransportationStandardAmount(),
                day.getPhoneStandardAmount(), selected(day.getMealSelected()), selected(day.getTransportationSelected()),
                selected(day.getPhoneSelected()), day.getMealAmount(), day.getTransportationAmount(), day.getPhoneAmount());
    }

    private AllocationVO toAllocationVO(AllocationEntity allocation) {
        return new AllocationVO(allocation.getId(), allocation.getCompanyId(), allocation.getCompanyNo(),
                allocation.getCompanyName(), allocation.getProjectId(), allocation.getProjectNo(),
                allocation.getProjectName(), allocation.getAllocationRatio(), allocation.getAllocationAmount(),
                allocation.getRowOrder());
    }

    private ReimbursementListVO toListVO(ReimbursementEntity main) {
        ReimbursementStatus status = ReimbursementStatus.fromCode(main.getStatus());
        return new ReimbursementListVO(main.getId(), main.getReimNo(), status.getCode(), status.getLabel(),
                display(main.getReimburserName(), main.getReimburserNo()),
                display(main.getReimDepartmentName(), main.getReimDepartmentNo()), main.getReimCompanyName(),
                main.getBusinessTypeName(), main.getReimbursementTitle(), main.getBusinessTripReason(),
                main.getSubsidyTotal(), main.getCreationTime());
    }

    private ReimbursementActionVO toActionVO(ReimbursementEntity main) {
        ReimbursementStatus status = ReimbursementStatus.fromCode(main.getStatus());
        return new ReimbursementActionVO(main.getId(), main.getReimNo(), status.getCode(), status.getLabel());
    }

    private ReimbursementEntity requireVisible(Long id, CurrentUser user) {
        ReimbursementEntity entity = requireMain(id);
        if (!isAdmin(user) && !entity.getOwnerUserId().equals(user.userId())) {
            throw new BusinessException(403, "只能查看自己的报销单");
        }
        return entity;
    }

    private ReimbursementEntity requireEditable(Long id, CurrentUser user) {
        ReimbursementEntity entity = requireOwned(id, user);
        if (!Objects.equals(entity.getStatus(), ReimbursementStatus.DRAFT.getCode())) {
            throw new BusinessException("仅草稿报销单可编辑");
        }
        return entity;
    }

    private ReimbursementEntity requireOwned(Long id, CurrentUser user) {
        ReimbursementEntity entity = requireMain(id);
        if (!entity.getOwnerUserId().equals(user.userId())) {
            throw new BusinessException(403, "只能操作自己的报销单");
        }
        return entity;
    }

    private ReimbursementEntity requireMain(Long id) {
        ReimbursementEntity entity = reimbursementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "报销单不存在");
        }
        return entity;
    }

    private EmployeeEntity requireEmployee(Long id) {
        EmployeeEntity entity = employeeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("员工不存在");
        }
        return entity;
    }

    private DepartmentEntity requireDepartment(Long id) {
        DepartmentEntity entity = departmentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("报销部门不存在");
        }
        return entity;
    }

    private CompanyEntity requireCompany(Long id) {
        CompanyEntity entity = companyMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("费用归属公司不存在");
        }
        return entity;
    }

    private BusinessTypeEntity requireBusinessType(Long id) {
        BusinessTypeEntity entity = businessTypeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("业务类型不存在");
        }
        return entity;
    }

    private CityEntity requireCity(Long id) {
        CityEntity entity = cityMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("城市不存在");
        }
        return entity;
    }

    private ProjectEntity requireProject(Long id) {
        ProjectEntity entity = projectMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("项目不存在");
        }
        return entity;
    }

    private static String display(String name, String no) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return StringUtils.hasText(no) ? name + "[" + no + "]" : name;
    }

    private static void setMainTotals(ReimbursementEntity main, BigDecimal subsidyTotal, BigDecimal meal,
                                      BigDecimal transportation, BigDecimal phone) {
        main.setSubsidyTotal(normalizeAmount(subsidyTotal));
        main.setMealAllowance(normalizeAmount(meal));
        main.setTransportationAllowance(normalizeAmount(transportation));
        main.setPhoneAllowance(normalizeAmount(phone));
    }

    private static BigDecimal mealStandard(Integer cityType) {
        return switch (cityType) {
            case 1 -> amount("100");
            case 2 -> amount("80");
            case 3 -> amount("50");
            default -> throw new BusinessException("城市类型不支持补助计算");
        };
    }

    private static BigDecimal resolveSubsidyAmount(boolean selected, BigDecimal provided, BigDecimal standard, String label) {
        if (!selected) {
            return ZERO;
        }
        BigDecimal actual = provided == null ? standard : normalizeAmount(provided);
        if (actual.compareTo(BigDecimal.ZERO) < 0 || actual.compareTo(standard) > 0) {
            throw new BusinessException(label + "金额必须在0到标准金额之间");
        }
        return actual;
    }

    private static BigDecimal selectedStandard(boolean selected, BigDecimal standard) {
        return selected ? standard : ZERO;
    }

    private static BigDecimal normalizeRatio(BigDecimal ratio) {
        BigDecimal normalized = ratio == null ? BigDecimal.ZERO : ratio.setScale(6, RoundingMode.HALF_UP);
        if (normalized.compareTo(BigDecimal.ZERO) < 0 || normalized.compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException("分摊比例必须在0到1之间");
        }
        return normalized;
    }

    private static BigDecimal normalizeAmount(BigDecimal value) {
        BigDecimal normalized = value == null ? ZERO : value.setScale(2, RoundingMode.HALF_UP);
        if (normalized.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("金额不能为负数");
        }
        return normalized;
    }

    private static BigDecimal add(BigDecimal... values) {
        BigDecimal result = ZERO;
        for (BigDecimal value : values) {
            result = result.add(value == null ? ZERO : value);
        }
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal amount(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    private static int flag(boolean value) {
        return value ? 1 : 0;
    }

    private static boolean selected(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    private static boolean selected(Integer value) {
        return value != null && value == 1;
    }

    private static String weekdayName(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE);
    }

    private static boolean isAdmin(CurrentUser user) {
        return user.hasRole("ADMIN");
    }

    private static String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static <T> List<T> safe(List<T> values) {
        return values == null ? List.of() : values;
    }

    private static void requireTripField(Object value, String label) {
        if (value == null) {
            throw new BusinessException(label + "不能为空");
        }
    }

    private static void requireMainField(Object value, String label) {
        if (value == null || value instanceof String text && !StringUtils.hasText(text)) {
            throw new BusinessException(label + "不能为空");
        }
    }

    private static final class Summary {
        private BigDecimal subsidyTotal = ZERO;
        private BigDecimal mealTotal = ZERO;
        private BigDecimal transportationTotal = ZERO;
        private BigDecimal phoneTotal = ZERO;

        private void add(SubsidyEntity subsidy) {
            subsidyTotal = ReimbursementService.add(subsidyTotal, subsidy.getSubsidyAmount());
            mealTotal = ReimbursementService.add(mealTotal, subsidy.getMealAmount());
            transportationTotal = ReimbursementService.add(transportationTotal, subsidy.getTransportationAmount());
            phoneTotal = ReimbursementService.add(phoneTotal, subsidy.getPhoneAmount());
        }
    }
}
