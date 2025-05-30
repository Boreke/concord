package org.concord.backend.config.jwt;

import lombok.Data;

@Data
public class TokenPayload {
    Long id;
    String role;
}
