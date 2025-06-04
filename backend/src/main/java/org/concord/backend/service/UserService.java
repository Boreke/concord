package org.concord.backend.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.concord.backend.config.jwt.JwtTokenUtil;
import org.concord.backend.dal.model.postgres.*;
import org.concord.backend.dal.postgres.repository.FollowRepository;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.UserPPRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.request.UserUpdateRequest;
import org.concord.backend.dto.response.UserResponse;
import org.concord.backend.dto.response.UserShortResponse;
import org.concord.backend.exceptions.http.HttpBadRequestException;
import org.concord.backend.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserPPRepository userPPRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    String userPPUrl = userPPRepository.findByUser(user)
                            .orElse(new UserPP(user, "https://concord-images.s3.eu-north-1.amazonaws.com/init/PPinit.webp"))
                            .getProfilePictureUrl();
                    return UserMapper.toResponse(user, userPPUrl);
                })
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    String userPPUrl = userPPRepository.findByUser(user)
                            .orElse(new UserPP(user, "https://concord-images.s3.eu-north-1.amazonaws.com/init/PPinit.webp"))
                            .getProfilePictureUrl();
                    return UserMapper.toResponse(user, userPPUrl);
                })
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public User getCurrentUserFromToken(String token) {
        DecodedJWT jwt = jwtTokenUtil.decodeToken(token);
        Long userId = Long.valueOf(jwt.getClaim("id").asString());
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public void follow(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new HttpBadRequestException("Follower not found"));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new HttpBadRequestException("Followee not found"));

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

    @Transactional(readOnly = true)
    public List<UserShortResponse> getRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> following = followRepository.findAllByFollower(user)
                .stream().map(Follow::getFollowee).toList();

        Set<Long> followingIds = following.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<Long> likedPostIds = user.getLikes().stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        boolean hasNoInteractions = likedPostIds.isEmpty() && following.isEmpty();

        List<User> sameLikers = likeRepository.findAll().stream()
                .filter(like -> likedPostIds.contains(like.getPost().getId()))
                .map(Like::getUser)
                .filter(u -> !u.getId().equals(userId))
                .toList();

        Map<Long, Integer> scoreMap = new HashMap<>();

        for (User u : sameLikers) {
            scoreMap.merge(u.getId(), 2, Integer::sum);
        }

        for (User u : following) {
            List<User> secondDegree = followRepository.findAllByFollower(u).stream()
                    .map(Follow::getFollowee)
                    .filter(f -> !f.getId().equals(userId))
                    .toList();
            for (User suggested : secondDegree) {
                scoreMap.merge(suggested.getId(), 1, Integer::sum);
            }
        }

        for (User u : following) {
            List<User> followersOfFollowee = followRepository.findAllByFollowee(u).stream()
                    .map(Follow::getFollower)
                    .filter(f -> !f.getId().equals(userId))
                    .toList();
            for (User suggested : followersOfFollowee) {
                scoreMap.merge(suggested.getId(), 1, Integer::sum);
            }
        }

        if (hasNoInteractions || scoreMap.isEmpty()) {
            List<Long> popularUserIds = followRepository.findAll().stream()
                    .map(f -> f.getFollowee().getId())
                    .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .filter(id -> !id.equals(userId))
                    .limit(20)
                    .toList();

            return popularUserIds.stream()
                    .map(id -> userRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .map(UserMapper::toShortResponse)
                    .toList();
        }

        return scoreMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(userId) && !followingIds.contains(entry.getKey()))
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(20)
                .map(entry -> userRepository.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .map(UserMapper::toShortResponse)
                .toList();
    }

    @Transactional()
    public User updateUser(Long id , UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setUserTag(userUpdateRequest.getUserTag());
        user.setEmail(userUpdateRequest.getEmail());
        user.setDisplayName(userUpdateRequest.getDisplayName());
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        user.setPrivate(userUpdateRequest.getIsPrivate());

        return userRepository.save(user);
    }

    public int getFollowersCount(Long id) {
        return followRepository.countByFolloweeId(id);
    }

    public int getFollowingCount(Long id) {
        return followRepository.countByFollowerId(id);
    }

    @Transactional(readOnly = true)
    public boolean isFollowed(Long id, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return followRepository.existsByFollowerAndFollowee(currentUser, user);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long id, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return followRepository.existsByFollowerAndFollowee(user, currentUser);
    }
}
