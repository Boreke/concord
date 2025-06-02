package org.concord.backend.mapper;

import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dto.response.PostResponse;

public class PostMapper {
    public static PostResponse toResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTitle(post.getTitle());
        postResponse.setContent(post.getContent());
        postResponse.setUserId(post.getUser().getId());
        postResponse.setUserTag(post.getUser().getUserTag());
        return postResponse;
    }
}