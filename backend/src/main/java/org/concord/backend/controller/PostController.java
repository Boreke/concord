package org.concord.backend.controller;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.request.UserIdRequest;
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

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }


    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likePost(@PathVariable Long id, @RequestBody UserIdRequest user) {
        postService.likePost(id, user.getCurrentUserId());
        return ResponseEntity.ok("Post liked");
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable Long id, @RequestBody UserIdRequest user) {
        postService.unlikePost(id, user.getCurrentUserId());
        return ResponseEntity.ok("Post unliked");
    }
}
