package com.event.ems.service.impl;

import com.event.ems.entity.Booking;
import com.event.ems.entity.Event;
import com.event.ems.entity.BookingStatus;
import com.event.ems.entity.EventType;
import com.event.ems.entity.PaymentStatus;
import com.event.ems.repository.BookingRepository;
import com.event.ems.repository.EventRepository;
import com.event.ems.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    @Override
    public Booking bookEvent(Long eventId, String userEmail) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Booking booking = new Booking();
        booking.setEventId(event.getId());
        booking.setEventTitle(event.getTitle());
        booking.setUserEmail(userEmail);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING);

        if (event.getEventType() == EventType.FREE) {
            booking.setAmount(0.0);
            booking.setPaymentStatus(PaymentStatus.NOT_REQUIRED);
        } else {
            booking.setAmount(event.getPrice());
            booking.setPaymentStatus(PaymentStatus.PENDING);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getUserBookings(String email) {
        return bookingRepository.findByUserEmail(email);
    }

    @Override
    public List<Booking> getBookingsByEvent(Long eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    @Override
    public Booking approveBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking rejectBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }
}
