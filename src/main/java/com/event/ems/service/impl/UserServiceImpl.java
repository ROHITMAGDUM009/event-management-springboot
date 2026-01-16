package com.event.ems.service.impl;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.LoginRequest;
import com.event.ems.dto.RegisterRequest;
import com.event.ems.entity.Role;
import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import com.event.ems.repository.RoleRepository;
import com.event.ems.repository.UserRepository;
import com.event.ems.security.JwtUtil;
import com.event.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil; // ✅ FIXED

    @Override
    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword()) // plain password
                .roles(Set.of(userRole))
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    // ✅ LOGIN
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String role = user.getRoles().iterator().next().getName().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        return new AuthResponse(token, role);
    }
}
