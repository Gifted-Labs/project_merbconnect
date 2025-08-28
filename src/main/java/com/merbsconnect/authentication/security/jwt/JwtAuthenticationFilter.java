package com.merbsconnect.authentication.security.jwt;

import com.merbsconnect.authentication.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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

            if(jwt != null) {
                log.debug("Validating JWT token...");
                boolean isValid = jwtService.validateToken(jwt);
                log.debug("JWT token validation result: {}", isValid);

                if(isValid) {
                    String username = jwtService.getUsernameFromToken(jwt);
                    log.debug("Username from token: {}", username);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    log.debug("User details loaded for: {}, authorities: {}", username, userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set in SecurityContext for user: {}", username);
                } else {
                    log.warn("JWT token validation failed for request: {}", requestPath);
                }
            } else {
                log.debug("No JWT token found in request headers for: {}", requestPath);
            }
        }
        catch (Exception e){
            log.error("Cannot set user authentication for request {}: {}", requestPath, e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/events/") ||
                path.startsWith("/api/password/") ||
                path.startsWith("/error") ||
                path.startsWith("/logout") ||
                path.startsWith("/signout") ||
                path.startsWith("/webjars/");
    }
}
