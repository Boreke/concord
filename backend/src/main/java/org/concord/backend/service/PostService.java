package org.concord.backend.service;

import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.postgres.repository.PostRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.response.PostResponse;
import org.concord.backend.exceptions.http.HttpBadRequestException;
import org.concord.backend.mapper.PostMapper;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostResponse createPost(PostRequest postRequest) {
        User user =  userRepository.findById(postRequest.getUserID())
                .orElseThrow(() -> new HttpBadRequestException("User not found"));
        Post post = PostMapper.toEntity(postRequest);
        return PostMapper.toDto(postRepository.save(post), user);
    }
}
