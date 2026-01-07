package com.merbsconnect.admin.service.impl;

import com.merbsconnect.admin.dto.request.UpdateConfigRequest;
import com.merbsconnect.admin.dto.response.ConfigResponse;
import com.merbsconnect.admin.model.SystemConfig;
import com.merbsconnect.admin.repository.SystemConfigRepository;
import com.merbsconnect.admin.service.AuditService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigServiceImpl Tests")
class ConfigServiceImplTest {

    @Mock
    private SystemConfigRepository configRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ConfigServiceImpl configService;

    @Test
    @DisplayName("getAllConfigs should return all configurations")
    void testGetAllConfigs() {
        // Arrange
        SystemConfig config = SystemConfig.builder()
                .configKey("TEST_KEY")
                .configValue("TEST_VALUE")
                .build();
        when(configRepository.findAll()).thenReturn(Collections.singletonList(config));

        // Act
        var result = configService.getAllConfigs();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConfigKey()).isEqualTo("TEST_KEY");
    }

    @Test
    @DisplayName("getConfig should return configuration when exists")
    void testGetConfig() {
        // Arrange
        SystemConfig config = SystemConfig.builder()
                .configKey("TEST_KEY")
                .configValue("TEST_VALUE")
                .build();
        when(configRepository.findByConfigKey("TEST_KEY")).thenReturn(Optional.of(config));

        // Act
        Optional<ConfigResponse> result = configService.getConfig("TEST_KEY");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getConfigValue()).isEqualTo("TEST_VALUE");
    }

    @Test
    @DisplayName("updateConfig should update and log audit trail")
    void testUpdateConfig() {
        // Arrange
        UpdateConfigRequest request = UpdateConfigRequest.builder()
                .configKey("TEST_KEY")
                .configValue("NEW_VALUE")
                .description("New Description")
                .build();

        SystemConfig config = SystemConfig.builder()
                .configKey("TEST_KEY")
                .configValue("OLD_VALUE")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        when(configRepository.findByConfigKey("TEST_KEY")).thenReturn(Optional.of(config));
        when(configRepository.save(any(SystemConfig.class))).thenReturn(config);

        // Act
        ConfigResponse result = configService.updateConfig(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getConfigValue()).isEqualTo("NEW_VALUE");
        verify(configRepository).save(any(SystemConfig.class));
        verify(auditService).logAction(eq("UPDATE"), eq("SystemConfig"), any(), anyString());
    }
}
