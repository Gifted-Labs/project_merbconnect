package com.merbsconnect.config;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value(("${app.admin.password}"))
    private String adminPassword;

    @Value(("${app.admin.phone}"))
    private String phoneNumber;

    public AdminUserConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ApplicationRunner initializeAdminUser() {
        return args -> {
            boolean adminExists = userRepository.existsByEmail(adminEmail);
            if (!adminExists) {
                User adminUser = new User();
                adminUser.setFirstName("Joseph");
                adminUser.setLastName("Asare");
                adminUser.setPhoneNumber(phoneNumber);
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setRole(UserRole.ROLE_ADMIN);
                adminUser.setEnabled(true);
                userRepository.save(adminUser);
                System.out.println("Admin user created with email: " + adminEmail);
            } else {
                System.out.println("Admin user already exists with email: " + adminEmail);
            }
        };
    }
}
