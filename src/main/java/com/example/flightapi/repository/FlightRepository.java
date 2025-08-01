package com.example.flightapi.repository;

import com.example.flightapi.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByFlightNumber(String flightNumber);

    // 新增：支持字符串参数和分页的查询
    @Query("SELECT f FROM Flight f WHERE f.departureAirport.code = :departure AND f.destinationAirport.code = :destination AND FUNCTION('DATE_FORMAT', f.departureDate, '%Y-%m-%d') = :date")
    Page<Flight> findByDepartureAndDestinationAndDate(String departure, String destination, String date, Pageable pageable);
}
