package org.concord.backend.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String password;
    private String userTag;
    private String displayName;
    private Boolean isPrivate;
}