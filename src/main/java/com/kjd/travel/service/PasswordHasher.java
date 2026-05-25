package com.kjd.travel.service;

import com.kjd.travel.config.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class PasswordHasher {

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 4 || !"pbkdf2_sha256".equals(parts[0])) {
                throw new BusinessException("密码配置不正确");
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = decode(parts[2]);
            byte[] expected = decode(parts[3]);
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, iterations, expected.length * 8);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return MessageDigest.isEqual(expected, actual);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("密码校验失败");
        }
    }

    private byte[] decode(String value) {
        int padding = (4 - value.length() % 4) % 4;
        return Base64.getUrlDecoder().decode(value + "=".repeat(padding));
    }
}
