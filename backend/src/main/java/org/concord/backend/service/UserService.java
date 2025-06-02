package org.concord.backend.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityNotFoundException;
import org.concord.backend.dal.model.enums.Role;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.request.UserRequest;
import org.concord.backend.dto.response.UserResponse;
import org.concord.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.concord.backend.config.jwt.JwtTokenUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public UserResponse createUser(UserRequest dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow();
    }

    public User getCurrentUserFromToken(String token) {
        DecodedJWT jwt = jwtTokenUtil.decodeToken(token);
        Long userId = Long.valueOf(jwt.getClaim("id").asString());
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void promoteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow();
        Role newRole = user.getRole() == Role.USER ? Role.ADMIN : Role.SUPER_ADMIN;
        user.setRole(newRole);
        userRepository.save(user);
    }
}
