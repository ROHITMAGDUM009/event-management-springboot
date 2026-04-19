package com.event.ems.repository;

import com.event.ems.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmail(String email);
    List<Booking> findByEvent_CreatedBy(String organizerEmail);
    boolean existsByUserEmailAndEvent_Id(String userEmail, Long eventId);
    void deleteByEvent_Id(Long eventId);

    // ✅ SEAT COUNT
    List<Booking> findByEvent_Id(Long eventId);

    // ✅ BOOKING COUNT FOR SUMMARY
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.id = :eventId AND b.bookingStatus != 'CANCELLED'")
    long countByEventIdAndNotCancelled(@Param("eventId") Long eventId);

    // ✅ REVENUE FOR SUMMARY
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.event.id = :eventId AND b.paymentStatus = 'SUCCESS'")
    double sumRevenueByEventId(@Param("eventId") Long eventId);
}