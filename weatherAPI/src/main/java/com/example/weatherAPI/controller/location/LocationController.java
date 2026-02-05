package com.example.weatherAPI.controller.location;

import com.example.weatherAPI.dto.location.LocationRequest;
import com.example.weatherAPI.entity.Location;
import com.example.weatherAPI.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<?> addLocation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LocationRequest request) {
        Location location = locationService.addLocation(userDetails.getUsername(), request);
        return ResponseEntity.ok(location);
    }

    @GetMapping
    public ResponseEntity<?> getLocations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                locationService.getUserLocations(userDetails.getUsername())
        );
    }
}
