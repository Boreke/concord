package org.concord.backend.dto.response;

import lombok.Data;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userTag;
    private long likeCount;
}
