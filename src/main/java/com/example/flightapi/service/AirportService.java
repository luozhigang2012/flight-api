package com.example.flightapi.service;

import com.example.flightapi.dto.airport.AirportResponseDTO;
import com.example.flightapi.entity.Airport;
import com.example.flightapi.mapper.AirportMapper;
import com.example.flightapi.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportService {
    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    public List<AirportResponseDTO> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return airportMapper.toDtoList(airports);
    }
}
