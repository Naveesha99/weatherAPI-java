package com.example.weatherAPI.config;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.weatherAPI.repository.UserRepository;
import com.example.weatherAPI.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getUsernameFromToken(token);
                log.debug("JWT valid. Subject(email)={} path={}", email, request.getRequestURI());
                Optional<com.example.weatherAPI.entity.User> optionalUser = userRepository.findByEmail(email);
                if (optionalUser.isPresent()) {
                    com.example.weatherAPI.entity.User appUser = optionalUser.get();
                    // Principal should expose application username so controllers can use it
                    // Note: relies on Lombok-generated getters
                    UserDetails principal = User.withUsername(appUser.getUsername())
                        .password("")
                        .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
                        .build();

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated as username={} for path={}", appUser.getUsername(), request.getRequestURI());
                } else {
                    log.debug("No user found for email {}. Leaving unauthenticated.", email);
                }
            } else {
                log.debug("Invalid JWT token for path={}", request.getRequestURI());
            }
        } else {
            if (StringUtils.hasText(header)) {
                log.debug("Authorization header present but not Bearer. header='{}' path={}", header, request.getRequestURI());
            } else {
                log.debug("No Authorization header. path={}", request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
    }
}
