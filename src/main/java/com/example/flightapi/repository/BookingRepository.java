package com.example.flightapi.repository;

import com.example.flightapi.entity.Booking;
import com.example.flightapi.entity.BookingStatus;
import com.example.flightapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    Optional<Booking> findByReference(String reference);

    // 新增：分页和状态查询
    Page<Booking> findByUser(User user, Pageable pageable);
    Page<Booking> findByUserAndStatus(User user, BookingStatus status, Pageable pageable);
}
