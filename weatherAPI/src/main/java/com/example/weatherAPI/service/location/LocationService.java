package com.example.weatherAPI.service.location;

import com.example.weatherAPI.dto.location.LocationRequest;
import com.example.weatherAPI.entity.Location;
import com.example.weatherAPI.entity.User;
import com.example.weatherAPI.repository.LocationRepository;
import com.example.weatherAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public Location addLocation(String username, LocationRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If setting default → unset previous default
        if (request.isDefault()) {
            locationRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(loc -> {
                        loc.setDefault(false);
                        locationRepository.save(loc);
                    });
        }

        Location location = new Location();
        location.setName(request.getName());
        location.setCity(request.getCity());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setDefault(request.isDefault());
        location.setUser(user);

        return locationRepository.save(location);
    }

    public List<Location> getUserLocations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return locationRepository.findByUserId(user.getId());
    }
}
