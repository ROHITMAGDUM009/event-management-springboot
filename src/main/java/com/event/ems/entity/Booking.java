package com.event.ems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Double amount;

    // ✅ TICKET QUANTITY (PHASE 4)
    private Integer ticketQuantity = 1;

    // ✅ CANCELLATION (PHASE 2)
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private Double refundAmount;
    private PaymentStatus refundStatus;
}