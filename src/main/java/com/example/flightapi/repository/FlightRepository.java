package com.example.flightapi.repository;

import com.example.flightapi.entity.Airport;
import com.example.flightapi.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByDepartureAirportAndDestinationAirportAndDepartureDate(
            Airport departureAirport, 
            Airport destinationAirport, 
            LocalDate departureDate);
            
    List<Flight> findByFlightNumber(String flightNumber);
}
