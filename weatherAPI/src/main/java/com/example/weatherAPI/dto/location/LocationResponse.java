package com.example.weatherAPI.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationResponse {

    private Long id;
    private String name;
    private String city;
    private Double latitude;
    private Double longitude;
    private boolean isDefault;
}
