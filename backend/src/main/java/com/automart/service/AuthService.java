package com.automart.service;

import com.automart.dto.AuthResponse;
import com.automart.dto.LoginRequest;
import com.automart.dto.RegisterRequest;
import com.automart.entity.Role;
import com.automart.entity.User;
import com.automart.exception.BadRequestException;
import com.automart.repository.UserRepository;
import com.automart.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        if (request.getRole() == Role.ADMIN) {
            // Prevent privilege escalation via self-registration.
            // Admin accounts are seeded directly in the database (see data.sql / README).
            throw new BadRequestException("Cannot self-register as ADMIN");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        // Delegates to Spring Security's AuthenticationManager, which uses
        // our DaoAuthenticationProvider (CustomUserDetailsService + BCrypt)
        // to verify the password. Throws BadCredentialsException on mismatch,
        // which GlobalExceptionHandler turns into a 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
