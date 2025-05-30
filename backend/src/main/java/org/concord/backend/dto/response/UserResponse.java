package org.concord.backend.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String userTag;
    private String fullName;
    private String role;
    private Boolean isVerified;
}