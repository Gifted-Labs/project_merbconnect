package com.merbsconnect.authentication.security.jwt;

import com.merbsconnect.authentication.security.CustomUserDetailsService;
import com.merbsconnect.util.EndpointUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath = request.getServletPath();
        log.debug("Processing request: {} {}", request.getMethod(), requestPath);

        // Skip filter for auth endpoints
        if (requestPath.startsWith("/api/v1/auth/")) {
            log.debug("Skipping JWT filter for auth endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = parseJwt(request);
            log.debug("Extracted JWT token: {}", jwt != null ? "Present" : "Missing");

            if (jwt != null) {
                log.debug("Validating JWT token...");
                boolean isValid = jwtService.validateToken(jwt);
                log.debug("JWT token validation result: {}", isValid);

                if (isValid) {
                    Claims claims = jwtService.getClaims(jwt);
                    String username = claims.getSubject();
                    log.debug("Username from token: {}", username);
                    String role = claims.get("role", String.class);

                    // Log the role at INFO level to help debug authorization issues
                    log.info("JWT Authentication - User: {}, Role from token: {}", username, role);

                    List<GrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(role));

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    log.debug("User details loaded for: {}, authorities: {}", username, userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities);
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set in SecurityContext for user: {}", username);
                } else {
                    log.warn("JWT token validation failed for request: {}", requestPath);
                }
            } else {
                log.debug("No JWT token found in request headers for: {}", requestPath);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication for request {}: {}", requestPath, e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);

            // Strip surrounding quotes if present (common issue with some clients)
            if (token != null && token.length() >= 2) {
                // Remove leading and trailing double quotes
                if (token.startsWith("\"") && token.endsWith("\"")) {
                    log.warn("JWT token was wrapped in quotes - stripping them. Check client-side token handling.");
                    token = token.substring(1, token.length() - 1);
                }
                // Also handle single quotes just in case
                else if (token.startsWith("'") && token.endsWith("'")) {
                    log.warn(
                            "JWT token was wrapped in single quotes - stripping them. Check client-side token handling.");
                    token = token.substring(1, token.length() - 1);
                }
            }

            // Trim any whitespace
            if (token != null) {
                token = token.trim();
            }

            return token;
        }
        return null;
    }

    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) throws
    // ServletException {
    // String path = request.getServletPath();
    // return EndpointUtils.PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    // }
}
