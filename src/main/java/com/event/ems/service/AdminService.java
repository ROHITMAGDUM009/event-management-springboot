package com.event.ems.service;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.entity.Event;
import java.util.List;

public interface AdminService {
    DashboardStatsDTO getDashboardStats();
    Event approveEvent(Long eventId);
    Event rejectEvent(Long eventId);
    List<Event> getAllEvents();

    // ✅ ADDED
    void deleteEvent(Long eventId, String reason, String adminEmail);
}