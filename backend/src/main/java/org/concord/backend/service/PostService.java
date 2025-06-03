package org.concord.backend.service;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.*;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.PostRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dal.postgres.repository.FollowRepository;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.response.PostResponse;
import org.concord.backend.mapper.PostMapper;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;


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

        // 1. Posts likés par le user
        List<Long> likedPostIds = likeRepository.findPostIdsByUserId(userId);

// 2. Tous les likes des autres utilisateurs
        List<Like> otherLikes = likeRepository.findAllByUserIdNot(userId);

// 3. On filtre les utilisateurs ayant au moins un post en commun liké
        Map<Long, Integer> userInCommon = new HashMap<>();
        for (Like like : otherLikes) {
            if (likedPostIds.contains(like.getPost().getId())) {
                userInCommon.put(like.getUser().getId(), userInCommon.getOrDefault(like.getUser().getId(), 0) + 1);
            }
        }

// 4. On prend les posts qu’ils ont likés hors de l’intersection
        Map<Post, Integer> scoreMap = new HashMap<>();
        for (Like like : otherLikes) {
            Long likerId = like.getUser().getId();
            Post post = like.getPost();

            if (userInCommon.containsKey(likerId) && !likedPostIds.contains(post.getId())) {
                scoreMap.put(post, scoreMap.getOrDefault(post, 0) + 1);
            }
        }


        System.out.println("Score Map Size: " + scoreMap.size());
        System.out.println("User Liked Posts: " + otherLikes.size());
        System.out.println("Friend Liked Posts: " + userInCommon.size());
        Map<Integer, Long> distribution = scoreMap.values().stream()
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        distribution.forEach((score, count) ->
                System.out.println("Posts with score " + score + ":" + count));

        if (scoreMap.isEmpty()) {
            List<Post> fallback = postRepository.findTop20ByOrderByCreatedAtDesc();
            return fallback.stream()
                    .map(PostMapper::toResponse)
                    .toList();
        }

        return scoreMap.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Post, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(e -> e.getKey().getCreatedAt(), Comparator.reverseOrder()))
                .limit(20)
                .map(e -> PostMapper.toResponse(e.getKey()))
                .toList();
    }
}
