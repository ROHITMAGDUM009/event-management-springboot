package com.event.ems.controller;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.entity.Event;
import com.event.ems.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ✅ DASHBOARD STATS
    @GetMapping("/dashboard-stats")
    public DashboardStatsDTO getDashboardStats() {
        return adminService.getDashboardStats();
    }

    // ✅ ALL EVENTS (for admin panel)
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return adminService.getAllEvents();
    }

    // ✅ APPROVE EVENT
    @PutMapping("/events/{id}/approve")
    public Event approveEvent(@PathVariable Long id) {
        return adminService.approveEvent(id);
    }

    // ✅ REJECT EVENT
    @PutMapping("/events/{id}/reject")
    public Event rejectEvent(@PathVariable Long id) {
        return adminService.rejectEvent(id);
    }
}