package com.kjd.travel.controller;

import com.kjd.travel.dto.LoginDTO;
import com.kjd.travel.service.AuthService;
import com.kjd.travel.vo.LoginVO;
import com.kjd.travel.vo.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }
}
