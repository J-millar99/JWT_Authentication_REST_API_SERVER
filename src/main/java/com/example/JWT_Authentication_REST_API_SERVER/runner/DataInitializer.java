package com.example.JWT_Authentication_REST_API_SERVER.runner;

import com.example.JWT_Authentication_REST_API_SERVER.Entity.Role;
import com.example.JWT_Authentication_REST_API_SERVER.Entity.User;
import com.example.JWT_Authentication_REST_API_SERVER.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminName;
    private final String adminPhone;
    private final String adminEmail;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.data-initializer.admin.username}") String adminUsername,
            @Value("${app.data-initializer.admin.password}") String adminPassword,
            @Value("${app.data-initializer.admin.name}") String adminName,
            @Value("${app.data-initializer.admin.phone}") String adminPhone,
            @Value("${app.data-initializer.admin.email}") String adminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
        this.adminPhone = adminPhone;
        this.adminEmail = adminEmail;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .name(adminName)
                    .phone(adminPhone)
                    .email(adminEmail)
                    .build();
            userRepository.save(admin);
        }
    }
}
