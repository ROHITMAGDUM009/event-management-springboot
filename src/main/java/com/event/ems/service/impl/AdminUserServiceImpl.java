package com.event.ems.service.impl;

import com.event.ems.dto.UserResponse;
import com.event.ems.entity.Role;
import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import com.event.ems.repository.EventRepository;
import com.event.ems.repository.RoleRepository;
import com.event.ems.repository.UserRepository;
import com.event.ems.service.AdminUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;

    public AdminUserServiceImpl(UserRepository userRepository,
                                RoleRepository roleRepository,
                                EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.eventRepository = eventRepository;
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

    // ✅ RENAMED — Demote instead of Remove
    @Override
    public void makeOrganizer(Long userId) {
        User user = getUser(userId);
        Role organizerRole = roleRepository
                .findByName(RoleName.ROLE_ORGANIZER)
                .orElseThrow(() -> new RuntimeException("ROLE_ORGANIZER not found"));
        user.getRoles().clear();
        user.getRoles().add(organizerRole);
        userRepository.save(user);
    }

    // ✅ RENAMED — Demote instead of Remove
    @Override
    public void removeOrganizer(Long userId) {
        User user = getUser(userId);

        // ✅ CHECK IF HAS EVENTS
        long eventCount = eventRepository.countByCreatedBy(user.getEmail());
        if (eventCount > 0) {
            throw new RuntimeException(
                    String.format(
                            "Cannot demote. Organizer has %d events. " +
                                    "Please transfer or delete events first.",
                            eventCount
                    )
            );
        }

        Role userRole = roleRepository
                .findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        user.getRoles().clear();
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUser(userId);

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);
        if (isAdmin) {
            throw new RuntimeException("Cannot delete admin user");
        }

        long eventCount = eventRepository.countByCreatedBy(user.getEmail());
        if (eventCount > 0) {
            throw new RuntimeException(
                    String.format(
                            "Cannot delete. User has %d events. Delete events first.",
                            eventCount
                    )
            );
        }

        userRepository.deleteById(userId);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    private UserResponse mapToResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setEnabled(user.isEnabled());
        String role = user.getRoles().isEmpty()
                ? "ROLE_USER"
                : user.getRoles().iterator().next().getName().name();
        res.setRole(role);
        return res;
    }
}