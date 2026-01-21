package com.example.weatherAPI.controller.auth;

import com.example.weatherAPI.dto.auth.SignupRequest;
import com.example.weatherAPI.entity.User;
import com.example.weatherAPI.entity.VerificationToken;
import com.example.weatherAPI.repository.UserRepository;
import com.example.weatherAPI.repository.VerificationTokenRepository;
import com.example.weatherAPI.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("User registered successfully. Please verify your email.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully");
    }
}
