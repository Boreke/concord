package org.concord.backend.controller;

import org.concord.backend.dal.postgres.repository.UserPPRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.service.S3Service;
import org.concord.backend.service.UserPPService;
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

    public UserPPControler(UserPPService userPPService, S3Service s3Service, UserPPRepository userPPRepository, UserRepository userRepository) {
        this.userPPService = userPPService;
        this.s3Service = s3Service;
        this.userPPRepository = userPPRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(userPPService.uploadImage(userId, file));
    }
}
