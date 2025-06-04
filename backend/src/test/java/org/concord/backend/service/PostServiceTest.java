package org.concord.backend.service;

import org.concord.backend.dal.model.postgres.Like;
import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.LikeRepository;
import org.concord.backend.dal.postgres.repository.PostRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.response.PostResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private PostService postService;

    public PostServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnRecommendedPostsFromSimilarUsers() {
        User user = new User();
        user.setId(1L);

        Post likedPost = new Post();
        likedPost.setId(100L);

        Like otherLike = new Like();
        User otherUser = new User();
        otherUser.setId(2L);
        otherLike.setUser(otherUser);
        otherLike.setPost(likedPost);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.findPostIdsByUserId(1L)).thenReturn(List.of(100L));
        when(likeRepository.findAllByUserIdNot(1L)).thenReturn(List.of(otherLike));

        List<PostResponse> recommendations = postService.getRecommendedPosts(1L);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
}