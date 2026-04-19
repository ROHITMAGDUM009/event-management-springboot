package com.event.ems.service.impl;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.LoginRequest;
import com.event.ems.dto.RegisterRequest;
import com.event.ems.dto.UserResponse;
import com.event.ems.entity.Role;
import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import com.event.ems.exception.ResourceNotFoundException;
import com.event.ems.exception.UserAlreadyExistsException;
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
    private final JwtUtil        jwtUtil;

    // ─── REGISTER ────────────────────────────────────────────────────────────
    @Override
    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // ✅ FIXED — accept both "ROLE_ORGANIZER" and "ORGANIZER"
        RoleName roleName;
        String rawRole = request.getRole();

        if ("ROLE_ORGANIZER".equalsIgnoreCase(rawRole) ||
                "ORGANIZER".equalsIgnoreCase(rawRole)) {
            roleName = RoleName.ROLE_ORGANIZER;
        } else {
            roleName = RoleName.ROLE_USER;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found: " + roleName));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(Set.of(role))
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException(
                    "Account is disabled. Contact administrator.");
        }

        String role = user.getRoles()
                .iterator()
                .next()
                .getName()
                .name();

        String token = jwtUtil.generateToken(user.getEmail(), role);

        // ✅ FIXED — return fullName + email too
        return new AuthResponse(token, role,
                user.getEmail(), user.getFullName());
    }

    // ─── PROFILE ──────────────────────────────────────────────────────────────
    @Override
    public UserResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRoles()
                .iterator()
                .next()
                .getName()
                .name();

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setEnabled(user.isEnabled());
        res.setRole(role);
        return res;
    }
}