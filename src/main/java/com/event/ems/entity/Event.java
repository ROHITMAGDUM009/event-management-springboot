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

    private Double price;

    @Enumerated(EnumType.STRING)
    private EventType eventType; // FREE, PAID, PACKAGE

    @Enumerated(EnumType.STRING)
    private BookingApprovalType approvalType; // AUTO / MANUAL

    @Enumerated(EnumType.STRING)
    private EventStatus status; // PENDING / APPROVED / REJECTED

    private String createdBy; // organizer email
}
