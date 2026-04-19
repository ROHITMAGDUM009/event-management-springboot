package com.event.ems.service.impl;

import com.event.ems.dto.BookingRequest;
import com.event.ems.entity.*;
import com.event.ems.repository.BookingRepository;
import com.event.ems.repository.EventRepository;
import com.event.ems.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    @Override
    public List<Booking> getMyBookings(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    @Transactional
    public Booking createBooking(BookingRequest request, String userEmail) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ CHECK APPROVED
        if (event.getStatus() != EventStatus.APPROVED) {
            throw new RuntimeException("Cannot book. Event is not approved yet.");
        }

        // ✅ CHECK DELETED
        if (event.isDeleted()) {
            throw new RuntimeException("Cannot book. Event has been cancelled.");
        }

        // ✅ TICKET QUANTITY
        Integer qty = request.getTicketQuantity() != null ? request.getTicketQuantity() : 1;

        if (qty < 1) {
            throw new RuntimeException("Ticket quantity must be at least 1");
        }

        if (qty > 10) {
            throw new RuntimeException("Maximum 10 tickets allowed per booking");
        }

        // ✅ SEAT AVAILABILITY CHECK
        if (event.getHasSeatLimit() && event.getTotalSeats() != null) {
            List<Booking> existingBookings = bookingRepository.findByEvent_Id(event.getId());
            int bookedSeats = existingBookings.stream()
                    .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                    .mapToInt(b -> b.getTicketQuantity() != null ? b.getTicketQuantity() : 1)
                    .sum();

            int availableSeats = event.getTotalSeats() - bookedSeats;

            if (qty > availableSeats) {
                throw new RuntimeException(
                        String.format("Only %d seats available. You requested %d.", availableSeats, qty)
                );
            }
        }

        // ✅ DUPLICATE CHECK
        if (bookingRepository.existsByUserEmailAndEvent_Id(userEmail, request.getEventId())) {
            throw new RuntimeException(
                    "You have already booked this event. Contact organizer to modify booking."
            );
        }

        // ✅ BUILD BOOKING
        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUserEmail(userEmail);
        booking.setTicketQuantity(qty);

        // Approval logic
        if (event.getApprovalType() == BookingApprovalType.AUTO) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.PENDING);
        }

        // Payment logic
        Double price = event.getPrice() != null ? event.getPrice() : 0.0;
        Double totalAmount = price * qty;

        if (price == 0.0) {
            booking.setPaymentStatus(PaymentStatus.NOT_REQUIRED);
            booking.setAmount(0.0);
        } else {
            booking.setPaymentStatus(PaymentStatus.PENDING);
            booking.setAmount(totalAmount);
        }

        return bookingRepository.save(booking);
    }

    // ✅ NEW — CANCEL BOOKING
    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId, String reason, String userEmail) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ✅ VERIFY OWNERSHIP
        if (!booking.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Not your booking");
        }

        // ✅ ALREADY CANCELLED?
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }

        // ✅ CHECK DEADLINE (48 hours before event)
        LocalDateTime eventDateTime = booking.getEvent().getEventDate().atStartOfDay();
        LocalDateTime deadline = eventDateTime.minusHours(48);

        if (LocalDateTime.now().isAfter(deadline)) {
            throw new RuntimeException(
                    "Cannot cancel. Less than 48 hours before event start time."
            );
        }

        // ✅ PROCESS CANCELLATION
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(reason);

        // ✅ REFUND LOGIC
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            long daysUntilEvent = java.time.Duration.between(
                    LocalDateTime.now(), eventDateTime
            ).toDays();

            if (daysUntilEvent >= 7) {
                booking.setRefundAmount(booking.getAmount()); // 100%
            } else if (daysUntilEvent >= 3) {
                booking.setRefundAmount(booking.getAmount() * 0.5); // 50%
            } else {
                booking.setRefundAmount(0.0); // No refund
            }
            booking.setRefundStatus(PaymentStatus.PENDING);
            // TODO: Call payment gateway refund API
        }

        return bookingRepository.save(booking);
    }
}