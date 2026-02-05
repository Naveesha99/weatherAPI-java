package com.example.weatherAPI.dto.location;

import lombok.Data;

@Data
public class LocationRequest {
    private String name;
    private String city;
    private Double latitude;
    private Double longitude;
    private boolean isDefault;
}
