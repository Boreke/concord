package org.concord.backend.mapper;

import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dto.request.PostRequest;
import org.concord.backend.dto.response.PostResponse;

import java.time.LocalDateTime;

public class PostMapper {
    public static Post toEntity(PostRequest dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getDescription());
        post.setUserId(dto.getUserID());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

    public static PostResponse toDto(Post post, User user) {
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getContent());
        dto.setUserID(post.getUserId());
        dto.setUserTag(user.getUserTag());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
