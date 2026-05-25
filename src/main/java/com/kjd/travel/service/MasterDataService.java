package com.kjd.travel.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kjd.travel.entity.BusinessTypeEntity;
import com.kjd.travel.mapper.BusinessTypeMapper;
import com.kjd.travel.mapper.CityMapper;
import com.kjd.travel.mapper.CompanyMapper;
import com.kjd.travel.mapper.DepartmentMapper;
import com.kjd.travel.mapper.EmployeeMapper;
import com.kjd.travel.mapper.ProjectMapper;
import com.kjd.travel.vo.BusinessTypeTreeVO;
import com.kjd.travel.vo.CityVO;
import com.kjd.travel.vo.CompanyVO;
import com.kjd.travel.vo.DepartmentVO;
import com.kjd.travel.vo.EmployeeVO;
import com.kjd.travel.vo.ProjectVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MasterDataService {

    private final CompanyMapper companyMapper;
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;
    private final BusinessTypeMapper businessTypeMapper;
    private final CityMapper cityMapper;
    private final ProjectMapper projectMapper;

    public MasterDataService(CompanyMapper companyMapper, DepartmentMapper departmentMapper, EmployeeMapper employeeMapper,
                             BusinessTypeMapper businessTypeMapper, CityMapper cityMapper, ProjectMapper projectMapper) {
        this.companyMapper = companyMapper;
        this.departmentMapper = departmentMapper;
        this.employeeMapper = employeeMapper;
        this.businessTypeMapper = businessTypeMapper;
        this.cityMapper = cityMapper;
        this.projectMapper = projectMapper;
    }

    public List<CompanyVO> companies() {
        return companyMapper.selectList(Wrappers.lambdaQuery()).stream()
                .map(entity -> new CompanyVO(entity.getId(), entity.getCompanyNo(), entity.getCompanyName()))
                .toList();
    }

    public List<DepartmentVO> departments() {
        return departmentMapper.selectList(Wrappers.lambdaQuery()).stream()
                .map(entity -> new DepartmentVO(entity.getId(), entity.getDepartmentNo(), entity.getDepartmentName()))
                .toList();
    }

    public List<EmployeeVO> employees() {
        return employeeMapper.selectList(Wrappers.lambdaQuery()).stream()
                .map(entity -> new EmployeeVO(entity.getId(), entity.getEmployeeNo(), entity.getEmployeeName(),
                        entity.getDepartmentId(), entity.getCompanyId()))
                .toList();
    }

    public List<CityVO> cities() {
        return cityMapper.selectList(Wrappers.lambdaQuery()).stream()
                .map(entity -> new CityVO(entity.getId(), entity.getCityNo(), entity.getCityName(), entity.getCityType()))
                .toList();
    }

    public List<ProjectVO> projects() {
        return projectMapper.selectList(Wrappers.lambdaQuery()).stream()
                .map(entity -> new ProjectVO(entity.getId(), entity.getProjectNo(), entity.getProjectName()))
                .toList();
    }

    public List<BusinessTypeTreeVO> businessTypeTree() {
        List<BusinessTypeEntity> types = businessTypeMapper.selectList(Wrappers.lambdaQuery());
        Map<Long, List<BusinessTypeEntity>> childrenByParent = types.stream()
                .filter(type -> type.getParentId() != null)
                .collect(Collectors.groupingBy(BusinessTypeEntity::getParentId));
        return types.stream()
                .filter(type -> type.getParentId() == null)
                .map(type -> toTree(type, childrenByParent))
                .toList();
    }

    private BusinessTypeTreeVO toTree(BusinessTypeEntity type, Map<Long, List<BusinessTypeEntity>> childrenByParent) {
        List<BusinessTypeTreeVO> children = childrenByParent.getOrDefault(type.getId(), List.of()).stream()
                .map(child -> toTree(child, childrenByParent))
                .toList();
        return new BusinessTypeTreeVO(type.getId(), type.getBusinessTypeNo(), type.getBusinessTypeName(),
                type.getParentId(), type.getLeafFlag() == 1, children);
    }
}
