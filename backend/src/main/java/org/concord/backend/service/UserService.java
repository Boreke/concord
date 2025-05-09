package org.concord.backend.service;

import org.concord.backend.dto.PublicUserDTO;
import org.concord.backend.dto.UpdateProfileDTO;
import org.concord.backend.model.User;
import org.concord.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<User> getProfile(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<User> updateProfile(UserDetails userDetails, UpdateProfileDTO update) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(existing -> {
                    existing.setDisplayName(update.displayName());
                    existing.setBio(update.bio());
                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Page<PublicUserDTO>> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllByUsernameContainingIgnoreCase(query, pageable);
        List<PublicUserDTO> dtos = users.getContent().stream()
                .map(user -> new PublicUserDTO(user.getUsername(), user.getDisplayName(), user.getBio()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PageImpl<>(dtos, pageable, users.getTotalElements()));
    }
}