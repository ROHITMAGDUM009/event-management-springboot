package com.event.ems.controller;

import com.event.ems.entity.Booking;
import com.event.ems.entity.PaymentStatus;
import com.event.ems.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
public class PaymentController {

    private final BookingRepository bookingRepository;

    // Only allow paying for own bookings
    @PostMapping("/pay/{bookingId}")
    public Booking pay(@PathVariable Long bookingId, Authentication authentication) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify this booking belongs to the user
        if (!booking.getUserEmail().equals(authentication.getName())) {
            throw new RuntimeException("Not allowed");
        }

        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        return bookingRepository.save(booking);
    }
}