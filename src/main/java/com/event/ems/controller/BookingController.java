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
public class BookingController {

    private final BookingService bookingService;

    // USER
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Booking bookEvent(
            @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        return bookingService.bookEvent(request.getEventId(), authentication.getName());
    }

    // USER
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<Booking> myBookings(Authentication authentication) {
        return bookingService.getUserBookings(authentication.getName());
    }

    // ORGANIZER
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public Booking approve(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public Booking reject(@PathVariable Long id) {
        return bookingService.rejectBooking(id);
    }
}
