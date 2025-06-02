package org.concord.backend.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRequest {
    private Long userId;
    private String title;
    private String content;
}
