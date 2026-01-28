package com.merbsconnect.authentication.mapper;

import com.merbsconnect.authentication.dto.response.JwtResponse;
import com.merbsconnect.authentication.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationMapper {

    public static JwtResponse toJwtResponse(CustomUserDetails userDetails, String accessToken, String refreshToken) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JwtResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .roles(roles)
                .type("Bearer")
                .build();
    }
}
