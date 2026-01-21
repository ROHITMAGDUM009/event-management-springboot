package com.event.ems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    // For PAID events
    private Double price;

    // Who created this event
    private String createdBy; // email from JWT
}
