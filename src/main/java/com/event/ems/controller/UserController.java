package com.event.ems.controller;

import com.event.ems.dto.UserResponse;
import com.event.ems.entity.Event;
import com.event.ems.service.EventService;
import com.event.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    // ===============================
    // 1️⃣ GET LOGGED-IN USER PROFILE
    // ===============================
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public UserResponse getMyProfile(Authentication authentication) {
        return userService.getMyProfile(authentication.getName());
    }

    // ===============================
    // 2️⃣ VIEW ALL APPROVED EVENTS
    // ===============================
    @GetMapping("/events")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<Event> viewApprovedEvents() {
        return eventService.getApprovedEvents();
    }

}
