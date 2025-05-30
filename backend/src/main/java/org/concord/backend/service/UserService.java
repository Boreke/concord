package org.concord.backend.service;

import org.concord.backend.config.jwt.TokenPayload;
import org.concord.backend.dal.model.enums.Role;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.request.UserRequest;
import org.concord.backend.dto.response.UserResponse;
import org.concord.backend.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public UserResponse getCurrentUser() {
        var payload = (TokenPayload) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return userRepository.findById(payload.getId())
                .map(UserMapper::toDto)
                .orElseThrow();
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
