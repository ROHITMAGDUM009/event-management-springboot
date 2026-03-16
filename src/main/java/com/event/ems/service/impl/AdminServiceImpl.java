package com.event.ems.service.impl;

import com.event.ems.dto.DashboardStatsDTO;
import com.event.ems.dto.OrganizerDTO;
import com.event.ems.dto.RevenueDTO;
import com.event.ems.entity.Booking;
import com.event.ems.entity.Payment;
import com.event.ems.entity.User;
import com.event.ems.repository.*;
import com.event.ems.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            EventRepository eventRepository,
                            BookingRepository bookingRepository,
                            PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {

        long users = userRepository.countByRole("ROLE_USER");
        long organizers = userRepository.countByRole("ROLE_ORGANIZER");
        long events = eventRepository.count();
        long bookings = bookingRepository.count();
        double revenue = paymentRepository.sumRevenue();

        return new DashboardStatsDTO(users, organizers, events, bookings, revenue);
    }

    @Override
    public List<OrganizerDTO> getAllOrganizers() {

        return userRepository.findByRole("ROLE_ORGANIZER")
                .stream()
                .map(user -> new OrganizerDTO(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.isEnabled()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void approveOrganizer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void rejectOrganizer(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public RevenueDTO getRevenue() {
        double total = paymentRepository.sumRevenue();
        return new RevenueDTO(total);
    }
}