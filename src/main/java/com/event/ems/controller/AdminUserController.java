package com.event.ems.controller;

import com.event.ems.dto.UserResponse;
import com.event.ems.dto.UserStatusRequest;
import com.event.ems.service.AdminUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PutMapping("/{id}/status")
    public void changeUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusRequest request
    ) {
        adminUserService.changeUserStatus(id, request.isEnabled());
    }

    @PutMapping("/{id}/make-organizer")
    public void makeOrganizer(@PathVariable Long id) {
        adminUserService.makeOrganizer(id);
    }

    @PutMapping("/{id}/remove-organizer")
    public void removeOrganizer(@PathVariable Long id) {
        adminUserService.removeOrganizer(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }
}
