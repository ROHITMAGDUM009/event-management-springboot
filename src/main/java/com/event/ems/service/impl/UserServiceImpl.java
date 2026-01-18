package com.event.ems.service.impl;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.LoginRequest;
import com.event.ems.dto.RegisterRequest;
import com.event.ems.dto.UserResponse;
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

        RoleName roleName;

        if ("ORGANIZER".equalsIgnoreCase(request.getRole())) {
            roleName = RoleName.ROLE_ORGANIZER;
        } else {
            roleName = RoleName.ROLE_USER;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword()) // plain (for now)
                .roles(Set.of(role))
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

        String role = user.getRoles()
                .iterator()
                .next()
                .getName()
                .name();

        String token = jwtUtil.generateToken(user.getEmail(), role);

        return new AuthResponse(token, role);
    }

    @Override
    public UserResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRoles()
                .iterator()
                .next()
                .getName()
                .name();

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                role
        );
    }

}
