package org.concord.backend.controller;

import jakarta.validation.Valid;
import org.concord.backend.dto.PublicUserDTO;
import org.concord.backend.dto.UpdateProfileDTO;
import org.concord.backend.model.User;
import org.concord.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getProfile(userDetails);
    }

    @PatchMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                              @Valid @RequestBody UpdateProfileDTO update) {
        return userService.updateProfile(userDetails, update);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PublicUserDTO>> searchUsers(@RequestParam(defaultValue = "") String query,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return userService.searchUsers(query, page, size);
    }
}