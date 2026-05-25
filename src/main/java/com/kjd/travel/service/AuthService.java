package com.kjd.travel.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kjd.travel.dto.LoginDTO;
import com.kjd.travel.entity.UserEntity;
import com.kjd.travel.mapper.UserMapper;
import com.kjd.travel.vo.LoginVO;
import com.kjd.travel.config.exception.BusinessException;
import com.kjd.travel.config.security.CurrentUser;
import com.kjd.travel.config.security.JwtProperties;
import com.kjd.travel.config.security.JwtTokenService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final JwtTokenService tokenService;
    private final JwtProperties jwtProperties;

    public AuthService(UserMapper userMapper, PasswordHasher passwordHasher, JwtTokenService tokenService,
                       JwtProperties jwtProperties) {
        this.userMapper = userMapper;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.jwtProperties = jwtProperties;
    }

    public LoginVO login(LoginDTO dto) {
        UserEntity user = userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getUsername, dto.username()));
        if (user == null || user.getEnabled() == null || user.getEnabled() != 1
                || !passwordHasher.matches(dto.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        Set<String> roles = new LinkedHashSet<>(Arrays.stream(user.getRoles().split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .toList());
        CurrentUser currentUser = new CurrentUser(user.getId(), user.getEmployeeId(), user.getUsername(), roles);
        return new LoginVO(tokenService.createToken(currentUser), "Bearer", jwtProperties.expirationMinutes());
    }
}
