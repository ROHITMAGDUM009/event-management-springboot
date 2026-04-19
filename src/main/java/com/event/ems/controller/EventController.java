package com.event.ems.controller;

import com.event.ems.dto.EventRequest;
import com.event.ems.entity.Event;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;  // ✅ REQUIRED FOR multipart/form-data
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // ✅ NEW — CREATE EVENT WITH IMAGE UPLOAD
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Event createEvent(
            @RequestPart("event") String eventData,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        EventRequest request = mapper.readValue(eventData, EventRequest.class);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" +
                    image.getOriginalFilename().replaceAll("\\s+", "_");
            Path uploadPath = Paths.get("uploads/events");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.write(uploadPath.resolve(fileName), image.getBytes());
            imageUrl = "/uploads/events/" + fileName;
        }

        request.setImageUrl(imageUrl);
        return eventService.createEvent(request, authentication.getName());
    }

    // ORGANIZER ONLY
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

    // PUBLIC — approved events only
    @GetMapping("/approved")
    public List<Event> getApprovedEvents() {
        return eventService.getApprovedEvents();
    }

    // ORGANIZER — my events
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public List<Event> getMyEvents(Authentication authentication) {
        return eventService.getMyEvents(authentication.getName());
    }

    // GET SINGLE EVENT BY ID (approved only)
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }
}