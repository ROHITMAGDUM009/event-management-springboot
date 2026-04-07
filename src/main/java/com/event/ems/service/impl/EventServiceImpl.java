package com.event.ems.service.impl;

import com.event.ems.dto.EventRequest;
import com.event.ems.entity.BookingApprovalType;
import com.event.ems.entity.Event;
import com.event.ems.entity.EventStatus;
import com.event.ems.entity.EventType;
import com.event.ems.repository.EventRepository;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> getMyEvents(String email) {
        return eventRepository.findByCreatedBy(email);
    }

    @Override
    public Event createEvent(EventRequest request, String email) {

        Event event = new Event();

        // 🔹 BASIC DETAILS
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setCreatedBy(email);

        // 🔹 EVENT STATUS (Admin approval pending)
        event.setStatus(EventStatus.PENDING);

        // 🔹 EVENT TYPE (FREE / PAID / PACKAGE)
        EventType eventType = parseEventType(request.getEventType());
        event.setEventType(eventType);

        BookingApprovalType approvalType = parseApprovalType(request.getApprovalType());
        event.setApprovalType(approvalType);

        // 🔹 IMAGE (optional)
        event.setImageUrl(request.getImageUrl());

        // 🔹 PRICE LOGIC
        if (eventType == EventType.PAID) {
            if (request.getPrice() == null || request.getPrice() <= 0) {
                throw new RuntimeException("Paid events must have a price greater than zero");
            }
            event.setPrice(request.getPrice());
        } else {
            event.setPrice(0.0);
        }

        return eventRepository.save(event);
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
        if (event.getStatus() != EventStatus.APPROVED) {
            throw new RuntimeException("Event not available");
        }
        return event;
    }

    @Override
    public Event updateEvent(Long id, EventRequest request, String email) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreatedBy().equals(email)) {
            throw new RuntimeException("Not allowed");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        EventType eventType = parseEventType(request.getEventType());
        event.setEventType(eventType);
        event.setApprovalType(parseApprovalType(request.getApprovalType()));
        event.setImageUrl(request.getImageUrl());

        if (eventType == EventType.PAID) {
            if (request.getPrice() == null || request.getPrice() <= 0) {
                throw new RuntimeException("Paid events must have a price greater than zero");
            }
            event.setPrice(request.getPrice());
        } else {
            event.setPrice(0.0);
        }

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventType parseEventType(String rawEventType) {
        if (rawEventType == null || rawEventType.isBlank()) {
            throw new RuntimeException("Event type is required");
        }

        try {
            return EventType.valueOf(rawEventType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid event type: " + rawEventType);
        }
    }

    private BookingApprovalType parseApprovalType(String rawApprovalType) {
        if (rawApprovalType == null || rawApprovalType.isBlank()) {
            return BookingApprovalType.AUTO;
        }

        try {
            return BookingApprovalType.valueOf(rawApprovalType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid approval type: " + rawApprovalType);
        }
    }
}
