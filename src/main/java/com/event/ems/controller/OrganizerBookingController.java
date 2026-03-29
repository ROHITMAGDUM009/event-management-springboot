package com.event.ems.controller;

import com.event.ems.entity.Booking;
import com.event.ems.entity.BookingStatus;
import com.event.ems.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizer/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
public class OrganizerBookingController {

    private final BookingRepository bookingRepository;

    @GetMapping("/my")
    public List<Booking> getMyEventBookings(Authentication authentication) {
        return bookingRepository.findByEvent_CreatedBy(authentication.getName());
    }

    @PostMapping("/{id}/approve")
    public Booking approveBooking(@PathVariable Long id, Authentication authentication) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getEvent().getCreatedBy().equals(authentication.getName())) {
            throw new RuntimeException("Not allowed");
        }

        booking.setBookingStatus(BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }
}
