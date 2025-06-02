package org.concord.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.Like;
import org.concord.backend.dal.model.postgres.LikeId;
import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.PostRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.response.PostResponse;
import org.concord.backend.mapper.PostMapper;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(PostRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post = postRepository.save(post);

        return PostMapper.toResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllWithLikes()
                .stream()
                .map(PostMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        LikeId id = new LikeId(userId, postId);
        Like like = new Like(id, user, post);
        likeRepository.save(like);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public boolean hasUserLiked(Post post, User user) {
        return post.getLikedBy().contains(user);
    }
}
