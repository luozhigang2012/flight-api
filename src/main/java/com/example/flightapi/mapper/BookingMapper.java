package com.example.flightapi.mapper;

import com.example.flightapi.dto.PagedResponseDTO;
import com.example.flightapi.dto.booking.BookingResponseDTO;
import com.example.flightapi.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PassengerMapper.class})
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "flight.id", target = "flightId")
    @Mapping(source = "passengers", target = "passengers")
    BookingResponseDTO toDto(Booking booking);
    
    List<BookingResponseDTO> toDtoList(List<Booking> bookings);
    
    default Page<BookingResponseDTO> toDtoPage(Page<Booking> page) {
        return page.map(this::toDto);
    }
    
    default PagedResponseDTO<BookingResponseDTO> toPagedResponseDto(Page<Booking> page) {
        Page<BookingResponseDTO> dtoPage = toDtoPage(page);
        return PagedResponseDTO.fromPage(dtoPage);
    }
    
}
