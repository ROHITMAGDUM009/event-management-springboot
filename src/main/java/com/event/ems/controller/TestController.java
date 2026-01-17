package com.event.ems.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 🔓 Any logged-in user
    @GetMapping("/secure")
    public String secure() {
        return "Authenticated User Access";
    }

    // 👤 USER only
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userAccess() {
        return "USER role access granted";
    }

    // 👑 ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminAccess() {
        return "ADMIN role access granted";
    }
}
/*“Role-based authorization is implemented using Spring Security’s @PreAuthorize annotation.
JWT tokens carry role information, which is validated by a custom filter before granting access to secured endpoints.”*/