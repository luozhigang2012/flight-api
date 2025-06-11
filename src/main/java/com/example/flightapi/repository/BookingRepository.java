package com.example.flightapi.repository;

import com.example.flightapi.entity.Booking;
import com.example.flightapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    Optional<Booking> findByReference(String reference);
}
