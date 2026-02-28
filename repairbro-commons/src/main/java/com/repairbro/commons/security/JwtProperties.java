package com.repairbro.commons.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "repairbro.jwt")
public class JwtProperties {

    /** Base64-encoded HMAC secret. Must match the auth-service secret. */
    private String secret;

    /**
     * Access token expiration in milliseconds. Only used for validation context.
     */
    private long accessExpirationMs = 3_600_000; // 1 hour
}
