package com.event.ems.controller;

import com.event.ems.dto.EventRequest;
import com.event.ems.entity.Event;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // USER, ORGANIZER, ADMIN
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ORGANIZER','ROLE_ADMIN')")
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    // ORGANIZER ONLY
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public Event createEvent(
            @RequestBody EventRequest request,
            Authentication authentication
    ) {
        return eventService.createEvent(request, authentication.getName());
    }


    // ORGANIZER (own event)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public Event updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request,
            Authentication authentication
    ) {
        return eventService.updateEvent(id, request, authentication.getName());
    }

    // ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted");
    }

    // Add these two methods to your existing EventController:

    // ✅ PUBLIC — approved events only
    @GetMapping("/approved")
    public List<Event> getApprovedEvents() {
        return eventService.getApprovedEvents();
    }

    // ✅ ORGANIZER — my events
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public List<Event> getMyEvents(Authentication authentication) {
        return eventService.getMyEvents(authentication.getName());
    }
}
