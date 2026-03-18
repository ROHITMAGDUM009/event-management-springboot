package com.event.ems.service.impl;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.entity.*;
import com.event.ems.repository.*;
import com.event.ems.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final RoleRepository roleRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {

        // Count total users (non-admin, non-organizer)
        long totalUsers = userRepository.countByRoles_Name(RoleName.ROLE_USER);

        // Count organizers
        long totalOrganizers = userRepository.countByRoles_Name(RoleName.ROLE_ORGANIZER);

        // Count all events
        long totalEvents = eventRepository.count();

        // Count pending events waiting for approval
        long pendingEvents = eventRepository.countByStatus(EventStatus.PENDING);

        // Count all bookings
        long totalBookings = bookingRepository.count();

        // Sum all successful payment amounts
        double totalRevenue = bookingRepository.findAll()
                .stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                .sum();

        return new DashboardStatsDTO(
                totalUsers,
                totalOrganizers,
                totalEvents,
                pendingEvents,
                totalBookings,
                totalRevenue
        );
    }

    @Override
    public Event approveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.APPROVED);
        return eventRepository.save(event);
    }

    @Override
    public Event rejectEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.REJECTED);
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}