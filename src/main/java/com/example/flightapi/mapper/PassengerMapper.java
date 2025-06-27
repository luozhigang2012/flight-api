package com.example.flightapi.mapper;

import com.example.flightapi.dto.PassengerInfoDTO;
import com.example.flightapi.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);
    
    PassengerInfoDTO toDto(Passenger passenger);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    Passenger toEntity(PassengerInfoDTO passengerInfoDTO);
    
    List<PassengerInfoDTO> toDtoList(List<Passenger> passengers);
    
    List<Passenger> toEntityList(List<PassengerInfoDTO> passengerInfoDTOs);
}