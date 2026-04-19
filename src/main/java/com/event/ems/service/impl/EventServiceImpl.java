package com.event.ems.service.impl;

import com.event.ems.dto.EventRequest;
import com.event.ems.entity.*;
import com.event.ems.repository.EventRepository;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Event createEvent(EventRequest request, String email) {
        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setCreatedBy(email);
        event.setStatus(EventStatus.PENDING);
        event.setEventType(parseEventType(request.getEventType()));
        event.setApprovalType(parseApprovalType(request.getApprovalType()));
        event.setImageUrl(request.getImageUrl());

        // ✅ SEAT MANAGEMENT
        event.setHasSeatLimit(request.getHasSeatLimit() != null ? request.getHasSeatLimit() : false);
        if (event.getHasSeatLimit() && request.getTotalSeats() != null) {
            event.setTotalSeats(request.getTotalSeats());
        }

        // ✅ EDIT LOCK — Can edit until 7 days before event OR until first booking
        event.setLastEditableUntil(event.getEventDate().atStartOfDay().minusDays(7));

        // ✅ PRICE LOGIC
        EventType eventType = event.getEventType();
        if (eventType == EventType.PAID) {
            if (request.getPrice() == null || request.getPrice() <= 0) {
                throw new RuntimeException("Paid events must have a price > 0");
            }
            event.setPrice(request.getPrice());
        } else {
            event.setPrice(0.0);
        }

        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, EventRequest request, String email) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreatedBy().equals(email)) {
            throw new RuntimeException("Not allowed");
        }

        // ✅ EDIT LOCK CHECK
        if (!event.isEditable()) {
            throw new RuntimeException(
                    "Cannot edit event. Event is locked (less than 7 days before event or has bookings)."
            );
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setEventType(parseEventType(request.getEventType()));
        event.setApprovalType(parseApprovalType(request.getApprovalType()));
        event.setImageUrl(request.getImageUrl());
        event.setHasSeatLimit(request.getHasSeatLimit());
        event.setTotalSeats(request.getTotalSeats());

        EventType eventType = event.getEventType();
        if (eventType == EventType.PAID) {
            if (request.getPrice() == null || request.getPrice() <= 0) {
                throw new RuntimeException("Paid events must have a price > 0");
            }
            event.setPrice(request.getPrice());
        } else {
            event.setPrice(0.0);
        }

        return eventRepository.save(event);
    }

    @Override
    public List<Event> getMyEvents(String email) {
        return eventRepository.findByCreatedBy(email);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(EventStatus.APPROVED);
    }

    @Override
    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getStatus() != EventStatus.APPROVED && !event.getCreatedBy().equals(getCurrentEmail())) {
            throw new RuntimeException("Event not available");
        }
        return event;
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventType parseEventType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Event type is required");
        }
        try {
            return EventType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid event type: " + raw);
        }
    }

    private BookingApprovalType parseApprovalType(String raw) {
        if (raw == null || raw.isBlank()) {
            return BookingApprovalType.AUTO;
        }
        try {
            return BookingApprovalType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid approval type: " + raw);
        }
    }

    private String getCurrentEmail() {
        // Get from SecurityContext in real implementation
        return "";
    }
}