package org.concord.backend.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String displayName;
    private String email;
    private boolean isPrivate;
    private String role;
    private String userTag;
}