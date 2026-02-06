package com.event.ems.controller;

import com.event.ems.entity.Booking;
import com.event.ems.entity.PaymentStatus;
import com.event.ems.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BookingRepository bookingRepository;

    @PostMapping("/pay/{bookingId}")
    public Booking pay(@PathVariable Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        return bookingRepository.save(booking);
    }
}
