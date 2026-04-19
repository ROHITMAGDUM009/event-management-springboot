package com.event.ems.service;

import com.event.ems.dto.BookingRequest;
import com.event.ems.entity.Booking;
import java.util.List;

public interface BookingService {
    Booking createBooking(BookingRequest request, String userEmail);
    List<Booking> getMyBookings(String userEmail);

    // ✅ ADDED
    Booking cancelBooking(Long bookingId, String reason, String userEmail);
}