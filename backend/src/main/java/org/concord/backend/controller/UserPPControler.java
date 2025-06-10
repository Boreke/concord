package org.concord.backend.controller;

import org.concord.backend.dal.postgres.repository.UserPPRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.exceptions.http.HttpUnauthorizedException;
import org.concord.backend.service.S3Service;
import org.concord.backend.service.UserPPService;
import org.concord.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserPPControler {

    private final UserPPService userPPService;
    private final S3Service s3Service;
    private final UserPPRepository userPPRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public UserPPControler(UserPPService userPPService, S3Service s3Service, UserPPRepository userPPRepository, UserRepository userRepository, UserService userService) {
        this.userPPService = userPPService;
        this.s3Service = s3Service;
        this.userPPRepository = userPPRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestHeader("Authorization") String authHeader, @RequestParam("file") MultipartFile file) throws IOException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw  new HttpUnauthorizedException("No authorization header provided");
        }
        String token = authHeader.substring(7);
        Long userId = userService.getCurrentUserFromToken(token).getId();
        return ResponseEntity.ok(userPPService.uploadImage(userId, file));
    }
}
