package org.concord.backend.service;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.Follow;
import org.concord.backend.dal.model.postgres.FollowId;
import org.concord.backend.dal.model.postgres.Like;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.FollowRepository;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.response.UserResponse;
import org.concord.backend.dto.response.UserShortResponse;
import org.concord.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUserTag(String userTag) {
        return userRepository.findByUserTag(userTag);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void follow(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("Followee not found"));

        FollowId id = new FollowId(followerId, followeeId);
        Follow follow = new Follow(id, follower, followee);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        FollowId id = new FollowId(followerId, followeeId);
        followRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserShortResponse> getFollowersByUserId(Long userId) {
        return followRepository.findByFolloweeId(userId).stream()
                .map(f -> UserMapper.toShortResponse(f.getFollower()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserShortResponse> getFollowingByUserId(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(f -> UserMapper.toShortResponse(f.getFollowee()))
                .toList();
    }

    private UserShortResponse toShortResponse(User user) {
        UserShortResponse dto = new UserShortResponse();
        dto.setId(user.getId());
        dto.setDisplayName(user.getDisplayName());
        dto.setUserTag(user.getUserTag());
        return dto;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUserTag(String userTag) {
        return userRepository.existsByUserTag(userTag);
    }

    @Transactional(readOnly = true)
    public List<UserShortResponse> getRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> following = followRepository.findAllByFollower(user).stream()
                .map(Follow::getFollowee)
                .toList();

        Set<Long> likedPostIds = user.getLikes().stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        boolean hasNoInteractions = likedPostIds.isEmpty() && followRepository.findAllByFollower(user).isEmpty();

        List<User> sameLikers = likeRepository.findAll().stream()
                .filter(like -> likedPostIds.contains(like.getPost().getId()))
                .map(Like::getUser)
                .filter(likedUser -> !likedUser.getId().equals(userId))
                .toList();

        Map<Long, Integer> scoreMap = new HashMap<>();

        for (User u : sameLikers) {
            scoreMap.merge(u.getId(), 2, Integer::sum);
        }

        for (User u : following) {
            List<User> uFollowing = followRepository.findAllByFollower(u).stream()
                    .map(Follow::getFollowee)
                    .filter(f -> !f.getId().equals(userId))
                    .toList();
            for (User suggested : uFollowing) {
                scoreMap.merge(suggested.getId(), 1, Integer::sum);
            }
        }

        if (hasNoInteractions || scoreMap.isEmpty()) {
            List<Long> popularUserIds = followRepository.findAll().stream()
                    .map(f -> f.getFollowee().getId())
                    .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(20)
                    .map(Map.Entry::getKey)
                    .toList();

            return popularUserIds.stream()
                    .map(id -> userRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .map(UserMapper::toShortResponse)
                    .toList();
        }

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(20)
                .map(entry -> userRepository.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .map(UserMapper::toShortResponse)
                .toList();
    }

}
