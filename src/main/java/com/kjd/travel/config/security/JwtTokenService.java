package com.kjd.travel.config.security;

import com.kjd.travel.config.exception.BusinessException;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class JwtTokenService {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final JsonMapper jsonMapper;
    private final JwtProperties properties;

    public JwtTokenService(JsonMapper jsonMapper, JwtProperties properties) {
        this.jsonMapper = jsonMapper;
        this.properties = properties;
    }

    public String createToken(CurrentUser user) {
        Instant now = Instant.now();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.username());
        payload.put("uid", user.userId());
        payload.put("eid", user.employeeId());
        payload.put("roles", user.roles());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plus(properties.expirationMinutes(), ChronoUnit.MINUTES).getEpochSecond());
        String signingInput = encodeJson(header) + "." + encodeJson(payload);
        return signingInput + "." + URL_ENCODER.encodeToString(sign(signingInput));
    }

    public CurrentUser parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BusinessException(401, "令牌格式错误");
            }
            String signingInput = parts[0] + "." + parts[1];
            byte[] expected = sign(signingInput);
            byte[] actual = URL_DECODER.decode(parts[2]);
            if (!java.security.MessageDigest.isEqual(expected, actual)) {
                throw new BusinessException(401, "令牌签名无效");
            }
            Map<String, Object> payload = jsonMapper.readValue(URL_DECODER.decode(parts[1]), new TypeReference<>() {
            });
            long expiration = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() >= expiration) {
                throw new BusinessException(401, "令牌已过期");
            }
            Set<String> roles = new LinkedHashSet<>();
            Object payloadRoles = payload.get("roles");
            if (payloadRoles instanceof Iterable<?> iterable) {
                iterable.forEach(role -> roles.add(String.valueOf(role)));
            }
            return new CurrentUser(
                    ((Number) payload.get("uid")).longValue(),
                    ((Number) payload.get("eid")).longValue(),
                    String.valueOf(payload.get("sub")),
                    roles
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(401, "令牌解析失败");
        }
    }

    private String encodeJson(Object value) {
        try {
            return URL_ENCODER.encodeToString(jsonMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT serialization failed", exception);
        }
    }

    private byte[] sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
            return mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT signing failed", exception);
        }
    }
}
