package com.event.ems.service;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.dto.EventRequest;
import com.event.ems.entity.Event;

import java.util.List;

public interface AdminService {

    // Dashboard
    DashboardStatsDTO getDashboardStats();

    // Event approvals
    Event approveEvent(Long eventId);
    Event rejectEvent(Long eventId);
    List<Event> getAllEvents();
}