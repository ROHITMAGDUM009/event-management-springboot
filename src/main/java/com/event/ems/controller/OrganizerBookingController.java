package com.event.ems.controller;

import com.event.ems.entity.Booking;
import com.event.ems.entity.BookingStatus;
import com.event.ems.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizer/bookings")
@RequiredArgsConstructor
public class OrganizerBookingController {

    private final BookingRepository bookingRepository;

    @PostMapping("/{id}/approve")
    public Booking approveBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }
}
