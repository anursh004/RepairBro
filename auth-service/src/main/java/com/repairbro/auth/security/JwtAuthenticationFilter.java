package com.repairbro.auth.security;

import com.repairbro.auth.model.User;
import com.repairbro.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JWT filter that extracts permissions and groups from token claims
 * and maps them to Spring Security authorities for @PreAuthorize.
 *
 * Authority format:
 * PERM_{code} → for each permission (e.g. PERM_TICKET_CREATE)
 * GROUP_{name} → for each group (e.g. GROUP_Super Admin)
 * ROLE_{role} → legacy role authorities (e.g. ROLE_ADMIN)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                Claims claims = jwtTokenProvider.parseToken(token);
                UUID userId = UUID.fromString(claims.getSubject());

                User user = userRepository.findById(userId).orElse(null);

                if (user != null) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    // 1. Permission-based authorities (from JWT claims or DB)
                    @SuppressWarnings("unchecked")
                    List<String> permissions = claims.get("permissions", List.class);
                    if (permissions != null) {
                        permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority("PERM_" + perm)));
                    } else {
                        // Fallback: compute from DB if not in token (legacy tokens)
                        user.getAllPermissions()
                                .forEach(perm -> authorities.add(new SimpleGrantedAuthority("PERM_" + perm)));
                    }

                    // 2. Group-based authorities (from JWT claims or DB)
                    @SuppressWarnings("unchecked")
                    List<String> groups = claims.get("groups", List.class);
                    if (groups != null) {
                        groups.forEach(group -> authorities.add(new SimpleGrantedAuthority("GROUP_" + group)));
                    } else {
                        user.getGroupNames()
                                .forEach(group -> authorities.add(new SimpleGrantedAuthority("GROUP_" + group)));
                    }

                    // 3. Legacy role authorities (backwards compatibility)
                    user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name())));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
                            null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.error("Failed to set user authentication", e);
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
