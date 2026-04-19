package com.event.ems.service.impl;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.entity.*;
import com.event.ems.repository.*;
import com.event.ems.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        long totalUsers = userRepository.countByRoles_Name(RoleName.ROLE_USER);
        long totalOrganizers = userRepository.countByRoles_Name(RoleName.ROLE_ORGANIZER);
        long totalEvents = eventRepository.count();
        long pendingEvents = eventRepository.countByStatus(EventStatus.PENDING);
        long totalBookings = bookingRepository.count();

        double totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                .sum();

        return new DashboardStatsDTO(
                totalUsers, totalOrganizers, totalEvents,
                pendingEvents, totalBookings, totalRevenue
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

    // ✅ NEW — SOFT DELETE WITH VALIDATION
    @Override
    @Transactional
    public void deleteEvent(Long eventId, String reason, String adminEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ CHECK IF HAS BOOKINGS
        long bookingCount = bookingRepository.countByEventIdAndNotCancelled(eventId);

        if (bookingCount > 0) {
            throw new RuntimeException(
                    String.format(
                            "Cannot delete event with %d active bookings. " +
                                    "Please cancel all bookings first or contact support.",
                            bookingCount
                    )
            );
        }

        // ✅ SOFT DELETE
        event.setDeleted(true);
        event.setDeletedAt(LocalDateTime.now());
        event.setDeletedBy(adminEmail);
        event.setDeletionReason(reason);
        event.setStatus(EventStatus.REJECTED);

        eventRepository.save(event);
    }
}