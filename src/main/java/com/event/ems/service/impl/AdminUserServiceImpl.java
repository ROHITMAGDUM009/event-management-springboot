package com.event.ems.service.impl;

import com.event.ems.dto.UserResponse;
import com.event.ems.entity.Role;
import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import com.event.ems.repository.RoleRepository;
import com.event.ems.repository.UserRepository;
import com.event.ems.service.AdminUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminUserServiceImpl(UserRepository userRepository,
                                RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void changeUserStatus(Long userId, boolean enabled) {
        User user = getUser(userId);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void makeOrganizer(Long userId) {
        User user = getUser(userId);
        Role organizerRole = roleRepository
                .findByName(RoleName.ROLE_ORGANIZER)
                .orElseThrow();
        user.setRoles(Set.of(organizerRole));
        userRepository.save(user);
    }

    @Override
    public void removeOrganizer(Long userId) {
        User user = getUser(userId);
        Role userRole = roleRepository
                .findByName(RoleName.ROLE_USER)
                .orElseThrow();
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse mapToResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setEnabled(user.isEnabled());
        res.setRole(user.getRoles().iterator().next().getName().name());
        return res;
    }
}
