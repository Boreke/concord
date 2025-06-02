package org.concord.backend.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRequest {
    private String title;
    private String description;
    private LocalDateTime date;
    private Long userID;
}
