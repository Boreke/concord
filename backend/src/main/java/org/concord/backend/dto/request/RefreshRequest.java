package org.concord.backend.dto.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
