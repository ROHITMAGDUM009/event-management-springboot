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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminInitializer(UserRepository userRepository,
                            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        // 🔐 Check if admin already exists
        if (userRepository.existsByEmail("admin@ems.com")) {
            return;
        }

        // 🔑 Fetch ADMIN role
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        // 👑 Create admin user
        User admin = new User();
        admin.setFullName("System Admin");
        admin.setEmail("admin@ems.com");
        admin.setPassword("admin123"); // plain password (as per your project)
        admin.setRoles(Set.of(adminRole));
        admin.setEnabled(true);

        userRepository.save(admin);

        System.out.println("✅ Default Admin Created: admin@ems.com / admin123");
    }
}
