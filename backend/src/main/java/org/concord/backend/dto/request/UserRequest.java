package org.concord.backend.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String password;
    private String userTag;
    private String fullName;
    private String role;
    private Boolean isPrivate;
}