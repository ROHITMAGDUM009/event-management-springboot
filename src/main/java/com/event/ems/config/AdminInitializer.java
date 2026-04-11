package com.event.ems.config;

import com.event.ems.entity.Role;
import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import com.event.ems.repository.RoleRepository;
import com.event.ems.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class AdminInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@ems.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminInitializer(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        User existing = userRepository.findByEmail(ADMIN_EMAIL).orElse(null);

        if (existing != null) {
            // If the password is BCrypt-hashed, reset to plain text for compatibility
            if (existing.getPassword().startsWith("$2a") || existing.getPassword().startsWith("$2b")) {
                System.out.println("⚠️  Reverting admin password from BCrypt to plain text...");
                existing.setPassword(ADMIN_PASSWORD);
                userRepository.save(existing);
                System.out.println("✅ Admin password reset to plain text: " + ADMIN_PASSWORD);
            } else {
                System.out.println("✅ Admin user already configured with plain text password");
            }
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        User admin = User.builder()
                .fullName("System Admin")
                .email(ADMIN_EMAIL)
                .password(ADMIN_PASSWORD) // plain text
                .roles(Set.of(adminRole))
                .enabled(true)
                .build();

        userRepository.save(admin);
        System.out.println("✅ Default Admin Created: " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
    }
}
