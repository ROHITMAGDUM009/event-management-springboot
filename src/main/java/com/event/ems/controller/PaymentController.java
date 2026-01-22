package com.event.ems.controller;

import com.event.ems.entity.Booking;
import com.event.ems.entity.BookingStatus;
import com.event.ems.entity.PaymentStatus;
import com.event.ems.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BookingRepository bookingRepository;

    @PostMapping("/pay/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Booking pay(@PathVariable Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.NOT_REQUIRED) {
            throw new RuntimeException("Payment not required");
        }

        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setBookingStatus(BookingStatus.PAID);

        return bookingRepository.save(booking);
    }
}
