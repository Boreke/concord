package org.concord.backend.controller;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.model.postgres.UserPP;
import org.concord.backend.dal.postgres.repository.UserPPRepository;
import org.concord.backend.dto.request.UserIdRequest;
import org.concord.backend.dto.request.UserUpdateRequest;
import org.concord.backend.dto.response.UserResponse;
import org.concord.backend.dto.response.UserShortResponse;
import org.concord.backend.mapper.UserMapper;
import org.concord.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPPRepository userPPRepository;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> findMe(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        User user = userService.getCurrentUserFromToken(token);
        String userPPUrl = userPPRepository.findByUser(user)
                .orElse(new UserPP(user, "https://concord-images.s3.eu-north-1.amazonaws.com/init/PPinit.webp"))
                .getProfilePictureUrl();
        return ResponseEntity.ok(UserMapper.toResponse(user, userPPUrl));
    }

    @PostMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@RequestHeader("Authorization") String authHeader, @RequestBody UserUpdateRequest userUpdateRequest) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        User user = userService.getCurrentUserFromToken(token);
        User updatedUser = userService.updateUser(user.getId(), userUpdateRequest);
        String userPPUrl = userPPRepository.findByUser(updatedUser)
                .orElse(new UserPP(updatedUser, "https://concord-images.s3.eu-north-1.amazonaws.com/init/PPinit.webp"))
                .getProfilePictureUrl();
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser, userPPUrl));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<String> follow(@PathVariable Long id, @RequestBody UserIdRequest follower) {
        userService.follow(follower.getCurrentUserId(), id);
        return ResponseEntity.ok("Followed successfully");
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<String> unfollow(@PathVariable Long id, @RequestBody UserIdRequest follower) {
        userService.unfollow(follower.getCurrentUserId(), id);
        return ResponseEntity.ok("Unfollowed successfully");
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserShortResponse>> getFollowers(@PathVariable Long id) {
        List<UserShortResponse> followers = userService.getFollowersByUserId(id);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserShortResponse>> getFollowing(@PathVariable Long id) {
        List<UserShortResponse> following = userService.getFollowingByUserId(id);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<UserShortResponse>> getRecommendations(@PathVariable Long id) {
        List<UserShortResponse> recommendations = userService.getRecommendations(id);
        return ResponseEntity.ok(recommendations);
    }
} 
