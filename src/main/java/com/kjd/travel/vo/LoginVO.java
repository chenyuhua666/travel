package com.kjd.travel.vo;

public record LoginVO(String token, String tokenType, long expiresInMinutes) {
}
