package com.event.ems.config;

import com.event.ems.entity.Role;
import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import com.event.ems.repository.RoleRepository;
import com.event.ems.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Set;

@Configuration
@Order(2)
public class AdminInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL    = "admin@ems.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminInitializer(UserRepository userRepository,
                            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        // Already exists → skip
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            System.out.println("✅ Admin user already exists");
            return;
        }

        Role adminRole = roleRepository
                .findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException(
                        "ROLE_ADMIN not found — Did DataInitializer run first?"));

        User admin = User.builder()
                .fullName("System Admin")
                .email(ADMIN_EMAIL)
                .password(ADMIN_PASSWORD)
                .roles(Set.of(adminRole))
                .enabled(true)
                .build();

        userRepository.save(admin);
        System.out.println("✅ Default Admin Created: "
                + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
    }
}