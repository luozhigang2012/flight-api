package com.example.flightapi.mapper;

import com.example.flightapi.dto.PagedResponseDTO;
import com.example.flightapi.dto.flight.FlightResponseDTO;
import com.example.flightapi.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    @Mapping(source = "departureAirport", target = "departure", qualifiedByName = "airportToString")
    @Mapping(source = "destinationAirport", target = "destination", qualifiedByName = "airportToString")
    @Mapping(source = "departureDate", target = "departureDate", qualifiedByName = "localDateToString")
    @Mapping(source = "departureTime", target = "departureTime", qualifiedByName = "localTimeToString")
    FlightResponseDTO toDto(Flight flight);
    
    List<FlightResponseDTO> toDtoList(List<Flight> flights);
    
    default Page<FlightResponseDTO> toDtoPage(Page<Flight> page) {
        return page.map(this::toDto);
    }
    
    default PagedResponseDTO<FlightResponseDTO> toPagedResponseDto(Page<Flight> page) {
        Page<FlightResponseDTO> dtoPage = toDtoPage(page);
        return PagedResponseDTO.fromPage(dtoPage);
    }
    
    @Named("airportToString")
    default String airportToString(com.example.flightapi.entity.Airport airport) {
        if (airport == null) {
            return null;
        }
        return airport.getCode() + " - " + airport.getName() + ", " + airport.getCity();
    }
    
    @Named("localDateToString")
    default String localDateToString(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE);
    }
    
    @Named("localTimeToString")
    default String localTimeToString(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ISO_TIME);
    }
}
