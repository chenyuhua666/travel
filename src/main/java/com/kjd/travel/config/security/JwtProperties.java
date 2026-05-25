package com.kjd.travel.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "travel.jwt")
public record JwtProperties(String secret, long expirationMinutes) {
}
