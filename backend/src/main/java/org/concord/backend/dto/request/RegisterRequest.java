package org.concord.backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String userTag;
    private String fullName;
}