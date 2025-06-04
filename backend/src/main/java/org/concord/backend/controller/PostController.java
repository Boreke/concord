package org.concord.backend.controller;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.response.PostResponse;
import org.concord.backend.service.PostService;
import org.concord.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }


    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(postService.getAllPosts(authHeader));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likePost(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String token = authHeader.substring(7);
        User user = userService.getCurrentUserFromToken(token);
        postService.likePost(id, user.getId());
        return ResponseEntity.ok("Post liked");
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String token = authHeader.substring(7);
        User user = userService.getCurrentUserFromToken(token);
        postService.unlikePost(id, user.getId());
        return ResponseEntity.ok("Post unliked");
    }

    @GetMapping("/{id}/recommended-posts")
    public ResponseEntity<List<PostResponse>> getRecommendedPosts(@PathVariable Long id) {
        List<PostResponse> posts = postService.getRecommendedPosts(id);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PostResponse>> getPostByUserId(@PathVariable Long id, @RequestHeader ("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }
        String token = authHeader.substring(7);
        User user = userService.getCurrentUserFromToken(token);
        List<PostResponse> post = postService.getPostByUserId(id, user.getId());
        return ResponseEntity.ok(post);
    }
}
