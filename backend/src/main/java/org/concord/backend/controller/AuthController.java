package org.concord.backend.controller;

import jakarta.validation.Valid;
import org.concord.backend.annotation.PublicEndpoint;
import org.concord.backend.dto.request.AuthRequest;
import org.concord.backend.dto.response.AuthResponse;
import org.concord.backend.dto.request.RegisterRequest;
import org.concord.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.concord.backend.dto.request.RefreshRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PublicEndpoint
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PublicEndpoint
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PublicEndpoint
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

}