package com.merbsconnect.util;

import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class EndpointUtils {

    // Public endpoints (no authentication required)
    public static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/",
            "/api/v1/events/**",
            "/index.html",
            "/admin.html",
            "/home.html",
            "/index.html/api/auth/**",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            "/error",
            "/webjars/**"
    );

    // Protected endpoints (require authentication)
    public static final List<Endpoint> PROTECTED_ENDPOINTS = Arrays.asList(
            new Endpoint("/api/v1/events", HttpMethod.POST),
            new Endpoint("/api/v1/events/{eventId}", HttpMethod.PUT),
            new Endpoint("/api/v1/events/{eventId}", HttpMethod.DELETE),
            new Endpoint("/api/v1/events/{eventId}/speakers", HttpMethod.POST),
            new Endpoint("/api/v1/events/{eventId}/speakers", HttpMethod.DELETE),
            new Endpoint("/api/v1/events/{eventId}/speakers/update", HttpMethod.PUT),
            new Endpoint("/api/v1/events/{eventId}/registrations", HttpMethod.GET),
            new Endpoint("/api/v1/events/{eventId}/registrations/export", HttpMethod.GET)
    );

    // Inner class to represent an endpoint with its HTTP method
    public static class Endpoint {
        private final String path;
        private final HttpMethod method;

        public Endpoint(String path, HttpMethod method) {
            this.path = path;
            this.method = method;
        }

        public String getPath() {
            return path;
        }

        public HttpMethod getMethod() {
            return method;
        }
    }
}
