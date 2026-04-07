package com.event.ems.controller;

import com.event.ems.dto.BookingRequest;
import com.event.ems.entity.Booking;
import com.event.ems.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
public class BookingController {

    private final BookingService bookingService;

    // ✅ CREATE BOOKING
    @PostMapping
    public Booking bookEvent(
            @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        return bookingService.createBooking(request, authentication.getName());
    }

    // ✅ NEW — GET MY BOOKINGS
    @GetMapping("/my")
    public List<Booking> getMyBookings(Authentication authentication) {
        return bookingService.getMyBookings(authentication.getName());
    }
}