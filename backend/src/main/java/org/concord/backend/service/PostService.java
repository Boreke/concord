package org.concord.backend.service;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.*;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.PostRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dal.postgres.repository.FollowRepository;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.response.PostResponse;
import org.concord.backend.exceptions.http.HttpBadRequestException;
import org.concord.backend.mapper.PostMapper;
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
    private final FollowRepository followRepository;
    private final UserService userService;

    private Boolean isPostLikedByCurrentUser(Post post, Long userId) {
        return likeRepository.existsByUserIdAndPostId(userId, post.getId());
    }

    public PostResponse createPost(PostRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post = postRepository.save(post);

        return PostMapper.toResponse(post, false);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            User currentUser = userService.getCurrentUserFromToken(token);
            return postRepository.findAllWithLikes()
                    .stream()
                    .map(post -> PostMapper.toResponse(post, isPostLikedByCurrentUser(post, currentUser.getId())))
                    .collect(Collectors.toList());
        } else {
            throw new HttpBadRequestException("Unauthorized");
        }
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

    @Transactional(readOnly = true)
    public List<PostResponse> getRecommendedPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Long> likedPostIds = likeRepository.findPostIdsByUserId(userId);

        List<Like> otherLikes = likeRepository.findAllByUserIdNot(userId);

        Map<Long, Integer> userInCommon = new HashMap<>();

        for (Like like : otherLikes) {
            if (likedPostIds.contains(like.getPost().getId())) {
                userInCommon.put(like.getUser().getId(), userInCommon.getOrDefault(like.getUser().getId(), 0) + 1);
            }
        }

        Map<Post, Integer> scoreMap = new HashMap<>();
        for (Like like : otherLikes) {
            Long likerId = like.getUser().getId();
            Post post = like.getPost();

            if (!userInCommon.containsKey(likerId) || likedPostIds.contains(post.getId())) {
                scoreMap.put(post, scoreMap.getOrDefault(post, 0) + 1);
            }
        }

        if (scoreMap.isEmpty()) {
            List<Post> fallback = postRepository.findTop20ByOrderByCreatedAtDesc();
            return fallback.stream()
                    .map(post -> PostMapper.toResponse(post, isPostLikedByCurrentUser(post, userId)))
                    .toList();
        }

        return scoreMap.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Post, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(e -> e.getKey().getCreatedAt(), Comparator.reverseOrder()))
                .limit(20)
                .map(e -> PostMapper.toResponse(e.getKey(), isPostLikedByCurrentUser(e.getKey(), userId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostByUserId(Long id, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequestException("User not found"));

        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return posts.stream()
                .map(post -> PostMapper.toResponse(post, isPostLikedByCurrentUser(post, currentUserId)))
                .collect(Collectors.toList());
    }
}
