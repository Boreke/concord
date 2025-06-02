package org.concord.backend.dto.response;

import lombok.Data;

@Data
public class UserShortResponse {
    private Long id;
    private String displayName;
    private String userTag;
}
