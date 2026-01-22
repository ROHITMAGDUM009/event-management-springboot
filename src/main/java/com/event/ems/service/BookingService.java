package com.event.ems.service;

import com.event.ems.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking bookEvent(Long eventId, String userEmail);

    List<Booking> getUserBookings(String email);

    List<Booking> getBookingsByEvent(Long eventId);

    Booking approveBooking(Long bookingId);

    Booking rejectBooking(Long bookingId);
}
