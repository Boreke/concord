package org.concord.backend.service;

import org.concord.backend.config.jwt.JwtTokenUtil;
import org.concord.backend.dal.model.enums.Role;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.request.AuthRequest;
import org.concord.backend.dto.response.AuthResponse;
import org.concord.backend.dto.request.RegisterRequest;
import org.concord.backend.config.jwt.TokenSet;
import org.concord.backend.exceptions.http.HttpBadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByUserTag(request.getUserTag())
                .orElseThrow(() -> new HttpBadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new HttpBadRequestException("Invalid credentials");
        }

        TokenSet tokenSet = jwtTokenUtil.generateTokenSet(user);
        return new AuthResponse(tokenSet.getToken(), tokenSet.getRefreshToken());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new HttpBadRequestException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserTag(request.getUserTag());
        user.setDisplayName(request.getDisplayName());
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        TokenSet tokenSet = jwtTokenUtil.generateTokenSet(user);
        return new AuthResponse(tokenSet.getToken(), tokenSet.getRefreshToken());
    }

    public AuthResponse refreshToken(String refreshToken) {
        TokenSet tokenSet = jwtTokenUtil.refreshWithToken(refreshToken);
        return new AuthResponse(tokenSet.getToken(), tokenSet.getRefreshToken());
    }
}