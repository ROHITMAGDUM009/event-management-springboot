package com.event.ems.service;

import com.event.ems.dto.BookingRequest;
import com.event.ems.entity.Booking;

public interface BookingService {

    Booking createBooking(BookingRequest request, String userEmail);

}
