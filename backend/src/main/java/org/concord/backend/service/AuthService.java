package org.concord.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.concord.backend.annotation.Public;
import org.concord.backend.dto.AuthRequest;
import org.concord.backend.dto.AuthResponse;
import org.concord.backend.dto.RegisterRequest;
import org.concord.backend.model.User;
import org.concord.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Public
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setDisplayName(request.getDisplayName());
        newUser.setBio(request.getBio());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.saveAndFlush(newUser);
        String token = jwtService.generateToken(savedUser);
        return new AuthResponse(token);
    }

    public AuthResponse login(@Valid AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}