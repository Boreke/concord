package org.concord.backend.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String userTag;
    private String password;
}