package com.example.flightapi.service;

import com.example.flightapi.dto.PagedResponseDTO;
import com.example.flightapi.dto.flight.FlightResponseDTO;
import com.example.flightapi.entity.Flight;
import com.example.flightapi.mapper.FlightMapper;
import com.example.flightapi.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public PagedResponseDTO<FlightResponseDTO> searchFlights(String from, String to, String date, Pageable pageable) {
        Page<Flight> flights = flightRepository.findByDepartureAndDestinationAndDate(from, to, date, pageable);
        return flightMapper.toPagedResponseDto(flights);
    }

    public Optional<FlightResponseDTO> getFlightById(Long id) {
        return flightRepository.findById(id).map(flightMapper::toDto);
    }
}
