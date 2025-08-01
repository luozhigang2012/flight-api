package com.example.flightapi.mapper;

import com.example.flightapi.dto.airport.AirportResponseDTO;
import com.example.flightapi.entity.Airport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface AirportMapper {
    // 中国城市列表
    List<String> CHINESE_CITIES = Arrays.asList(
        "Beijing", "Shanghai", "Guangzhou", "Shenzhen", "Chengdu",
        "Chongqing", "Kunming", "Xi'an", "Hangzhou", "Nanjing", "Qingdao"
    );
    
    // 城市到国家的映射
    Map<String, String> CITY_TO_COUNTRY = new HashMap<String, String>() {{
        put("New York", "USA");
        put("London", "UK");
        put("Tokyo", "Japan");
        put("Paris", "France");
        put("Singapore", "Singapore");
        put("Dubai", "UAE");
        put("Los Angeles", "USA");
        put("Hong Kong", "China");
        put("Sydney", "Australia");
        put("Frankfurt", "Germany");
    }};
    
    @Mapping(target = "country", source = "city", qualifiedByName = "cityToCountry")
    AirportResponseDTO toDto(Airport airport);
    
    List<AirportResponseDTO> toDtoList(List<Airport> airports);
    
    @Named("cityToCountry")
    default String cityToCountry(String city) {
        if (city == null) {
            return null;
        }
        
        // 检查是否是中国城市
        if (CHINESE_CITIES.contains(city)) {
            return "China";
        }
        
        // 检查是否在映射表中
        if (CITY_TO_COUNTRY.containsKey(city)) {
            return CITY_TO_COUNTRY.get(city);
        }
        
        // 默认返回未知
        return "Unknown";
    }
}
