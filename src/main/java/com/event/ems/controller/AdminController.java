package com.event.ems.controller;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.entity.Event;
import com.event.ems.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard-stats")
    public DashboardStatsDTO getDashboardStats() {
        return adminService.getDashboardStats();
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return adminService.getAllEvents();
    }

    @PutMapping("/events/{id}/approve")
    public Event approveEvent(@PathVariable Long id) {
        return adminService.approveEvent(id);
    }

    @PutMapping("/events/{id}/reject")
    public Event rejectEvent(@PathVariable Long id) {
        return adminService.rejectEvent(id);
    }

    // ✅ NEW — DELETE WITH VALIDATION
    @PutMapping("/events/{id}/delete")
    public void deleteEvent(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        adminService.deleteEvent(
                id,
                body.get("reason"),
                authentication.getName()
        );
    }
}