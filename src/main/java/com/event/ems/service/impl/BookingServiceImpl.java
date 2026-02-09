package com.event.ems.service.impl;

import com.event.ems.dto.BookingRequest;
import com.event.ems.entity.*;
import com.event.ems.repository.BookingRepository;
import com.event.ems.repository.EventRepository;
import com.event.ems.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    @Override
    public Booking createBooking(BookingRequest request, String userEmail) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUserEmail(userEmail);

        // 🔥 APPROVAL LOGIC
        if (event.getApprovalType() == BookingApprovalType.AUTO) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.PENDING);
        }

        // 🔥 PAYMENT LOGIC
        if (event.getPrice() == 0) {
            booking.setPaymentStatus(PaymentStatus.NOT_REQUIRED);
            booking.setAmount(0.0);
        } else {
            booking.setPaymentStatus(PaymentStatus.PENDING);
            booking.setAmount(event.getPrice());
        }

        return bookingRepository.save(booking);
    }
}
