package com.event.ems.controller;

import com.event.ems.dto.BookingRequest;
import com.event.ems.entity.Booking;
import com.event.ems.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking bookEvent(
            @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        return bookingService.createBooking(request, authentication.getName());
    }
}
