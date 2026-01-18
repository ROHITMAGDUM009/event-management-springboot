package com.event.ems.repository;

import com.event.ems.entity.Event;
import com.event.ems.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCreatedBy(String email);
}
