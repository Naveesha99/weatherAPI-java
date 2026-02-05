package com.example.weatherAPI.controller.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.weatherAPI.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
	var user = userRepository.findByUsername(principal.getUsername())
		.orElseThrow(() -> new RuntimeException("User not found"));

	return ResponseEntity.ok(
		Map.of(
			"username", user.getUsername(),
			"email", user.getEmail(),
			"enabled", user.isEnabled()
		)
	);
    }
}
