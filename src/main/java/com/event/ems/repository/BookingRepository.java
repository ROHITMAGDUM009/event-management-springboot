package com.event.ems.repository;

import com.event.ems.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmail(String email);

    List<Booking> findByEvent_CreatedBy(String organizerEmail);
}
