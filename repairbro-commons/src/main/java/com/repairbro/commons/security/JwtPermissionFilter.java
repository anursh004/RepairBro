package com.repairbro.commons.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shared stateless JWT filter for all microservices (except auth-service which
 * has its own).
 *
 * Extracts permissions and groups directly from JWT claims — NO database calls.
 *
 * Authority naming:
 * PERM_{code} → for each permission (e.g. PERM_TICKET_CREATE)
 * GROUP_{name} → for each group (e.g. GROUP_Super Admin)
 */
@Slf4j
public class JwtPermissionFilter extends OncePerRequestFilter {

    private final SecretKey key;

    public JwtPermissionFilter(JwtProperties properties) {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String userId = claims.getSubject();
                String email = claims.get("email", String.class);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                // 1. Permission-based authorities
                @SuppressWarnings("unchecked")
                List<String> permissions = claims.get("permissions", List.class);
                if (permissions != null) {
                    permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority("PERM_" + perm)));
                }

                // 2. Group-based authorities
                @SuppressWarnings("unchecked")
                List<String> groups = claims.get("groups", List.class);
                if (groups != null) {
                    groups.forEach(group -> authorities.add(new SimpleGrantedAuthority("GROUP_" + group)));
                }

                // 3. Legacy role authorities
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                }

                // Build principal map with useful claim data
                Map<String, Object> principal = Map.of(
                        "userId", userId,
                        "email", email != null ? email : "",
                        "permissions", permissions != null ? permissions : List.of(),
                        "groups", groups != null ? groups : List.of());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
                        authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                log.debug("JWT validation failed: {}", e.getMessage());
                // Don't set auth — request continues as unauthenticated
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
