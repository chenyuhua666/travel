package com.kjd.travel.controller;

import com.kjd.travel.vo.Result;
import com.kjd.travel.service.MasterDataService;
import com.kjd.travel.vo.BusinessTypeTreeVO;
import com.kjd.travel.vo.CityVO;
import com.kjd.travel.vo.CompanyVO;
import com.kjd.travel.vo.DepartmentVO;
import com.kjd.travel.vo.EmployeeVO;
import com.kjd.travel.vo.ProjectVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final MasterDataService masterDataService;

    public MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    @GetMapping("/companies")
    public Result<List<CompanyVO>> companies() {
        return Result.success(masterDataService.companies());
    }

    @GetMapping("/departments")
    public Result<List<DepartmentVO>> departments() {
        return Result.success(masterDataService.departments());
    }

    @GetMapping("/employees")
    public Result<List<EmployeeVO>> employees() {
        return Result.success(masterDataService.employees());
    }

    @GetMapping("/business-types/tree")
    public Result<List<BusinessTypeTreeVO>> businessTypeTree() {
        return Result.success(masterDataService.businessTypeTree());
    }

    @GetMapping("/cities")
    public Result<List<CityVO>> cities() {
        return Result.success(masterDataService.cities());
    }

    @GetMapping("/projects")
    public Result<List<ProjectVO>> projects() {
        return Result.success(masterDataService.projects());
    }
}
