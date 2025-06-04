package org.concord.backend.service;

import org.concord.backend.dal.model.postgres.Follow;
import org.concord.backend.dal.model.postgres.Like;
import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.FollowRepository;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.response.UserShortResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnUserRecommendations() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        // Mock likes
        Post post = new Post();
        post.setId(10L);
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        user.setLikes(Set.of(like));

        // Mock followees
        User followee = new User();
        followee.setId(2L);

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowee(followee);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(followRepository.findAllByFollower(user)).thenReturn(List.of(follow));

        // Simulate other likes
        User similarUser = new User();
        similarUser.setId(3L);

        Like otherLike = new Like();
        otherLike.setUser(similarUser);
        otherLike.setPost(post);

        when(likeRepository.findAll()).thenReturn(List.of(otherLike));

        // Inject a real-like user repository for lookup in mapping phase
        when(userRepository.findById(3L)).thenReturn(Optional.of(similarUser));

        List<UserShortResponse> recommendations = userService.getRecommendations(userId);

        assertFalse(recommendations.isEmpty());
        assertEquals(3L, recommendations.get(0).getId());
    }

}