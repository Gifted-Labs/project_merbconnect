package com.merbsconnect.config;

import com.merbsconnect.authentication.security.CustomUserDetailsService;
import com.merbsconnect.authentication.security.jwt.JwtAuthenticationEntryPoint;
import com.merbsconnect.authentication.security.jwt.JwtAuthenticationFilter;
import com.merbsconnect.util.EndpointUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomUserDetailsService userDetailsService;
        private final JwtAuthenticationEntryPoint unauthorizedHandler;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager() {
                return new ProviderManager(authenticationProvider());
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> {
                                        auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                                        // Public auth endpoints
                                        auth.requestMatchers("/api/v1/auth/**").permitAll();

                                        // Public event viewing endpoints (GET requests) - anyone can view events
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events").permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll();

                                        // Public event registration - anyone can register (includes v2 with QR code)
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/events/*/register").permitAll();
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/events/*/register-v2")
                                                        .permitAll();

                                        // Public: View reviews, articles, gallery
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/*/reviews").permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/*/articles").permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/*/articles/**")
                                                        .permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/*/gallery").permitAll();
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/*/registration")
                                                        .permitAll();

                                        // Public T-shirt request submission - no login required
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/tshirt-requests").permitAll();

                                        // Admin-only T-shirt request management
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/tshirt-requests/**")
                                                        .hasAnyRole("ADMIN", "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.PATCH, "/api/v1/tshirt-requests/**")
                                                        .hasAnyRole("ADMIN", "SUPER_ADMIN", "SUPPORT_ADMIN");

                                        // Admin-only event management operations (includes SUPER_ADMIN and
                                        // SUPPORT_ADMIN)
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/events").hasAnyRole("ADMIN",
                                                        "SUPER_ADMIN",
                                                        "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.PUT, "/api/v1/events/**").hasAnyRole("ADMIN",
                                                        "SUPER_ADMIN",
                                                        "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.DELETE, "/api/v1/events/**").hasAnyRole("ADMIN",
                                                        "SUPER_ADMIN");

                                        // Admin-only: Articles, Gallery upload, Check-in
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/events/*/articles").hasAnyRole(
                                                        "ADMIN",
                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.PUT, "/api/v1/events/*/articles/*").hasAnyRole(
                                                        "ADMIN",
                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.DELETE, "/api/v1/events/*/articles/*")
                                                        .hasAnyRole("ADMIN",
                                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/events/*/gallery").hasAnyRole(
                                                        "ADMIN", "SUPER_ADMIN",
                                                        "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.DELETE, "/api/v1/events/*/gallery/*")
                                                        .hasAnyRole("ADMIN",
                                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.PUT, "/api/v1/events/*/gallery/drive-link")
                                                        .hasAnyRole("ADMIN",
                                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/events/*/check-in").hasAnyRole(
                                                        "ADMIN",
                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/events/*/check-in/stats")
                                                        .hasAnyRole("ADMIN",
                                                                        "SUPER_ADMIN", "SUPPORT_ADMIN");

                                        // Other public endpoints (swagger, static files, etc.)
                                        EndpointUtils.PUBLIC_ENDPOINTS.forEach(
                                                        endpoint -> auth.requestMatchers(endpoint).permitAll());

                                        // Protected endpoints with specific role requirements
                                        EndpointUtils.PROTECTED_ENDPOINTS.forEach(endpoint -> auth
                                                        .requestMatchers(endpoint.getMethod(), endpoint.getPath())
                                                        .hasRole("ADMIN"));

                                        // All other requests require authentication
                                        auth.anyRequest().authenticated();
                                })
                                .authenticationProvider(authenticationProvider())
                                .authenticationManager(authenticationManager())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration
                                .setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080",
                                                "http://localhost:5174",
                                                "http://localhost:5175", "https://merbsconnect.com",
                                                "https://www.merbsconnect.com",
                                                "https://admin.merbsconnect.com"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Auth-Token"));
                configuration.setMaxAge(3600L); // 1 hour
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

}
