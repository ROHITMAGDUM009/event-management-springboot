package com.event.ems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private BookingApprovalType approvalType;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String createdBy;
    private String imageUrl;

    // ✅ SEAT MANAGEMENT (PHASE 4)
    private Boolean hasSeatLimit = false;
    private Integer totalSeats;

    // ✅ SOFT DELETE (PHASE 1 - Admin Delete)
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private String deletionReason;

    // ✅ EDIT LOCK TIMESTAMP (PHASE 3)
    private LocalDateTime lastEditableUntil;

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // ✅ HELPER — Calculate available seats
    @Transient
    public Integer getAvailableSeats() {
        if (!hasSeatLimit || totalSeats == null) {
            return null; // Unlimited
        }
        int booked = bookings.stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .mapToInt(b -> b.getTicketQuantity() != null ? b.getTicketQuantity() : 1)
                .sum();
        return totalSeats - booked;
    }

    // ✅ HELPER — Check if editable
    @Transient
    public boolean isEditable() {
        if (status == EventStatus.PENDING) {
            return true;
        }
        if (lastEditableUntil != null && LocalDateTime.now().isBefore(lastEditableUntil)) {
            return true;
        }
        return false;
    }
}