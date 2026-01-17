package com.event.ems.service;

import com.event.ems.dto.EventRequest;
import com.event.ems.entity.Event;

import java.util.List;

public interface EventService {

    Event createEvent(EventRequest request, String email);

    List<Event> getAllEvents();

    Event updateEvent(Long id, EventRequest request, String email);

    void deleteEvent(Long id);
}
