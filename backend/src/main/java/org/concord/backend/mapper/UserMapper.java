package org.concord.backend.mapper;

import org.concord.backend.dal.model.enums.Role;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dto.response.UserResponse;
import org.concord.backend.dto.response.UserShortResponse;

public class UserMapper {
    public static UserResponse toResponse(User user, String url) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setDisplayName(user.getDisplayName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPrivate(user.isPrivate());
        userResponse.setRole(user.getRole() != null ? user.getRole().name() : Role.ROLE_USER.name());
        userResponse.setUserTag(user.getUserTag());
        userResponse.setProfileImageUrl(url);
        return userResponse;
    }
    public static UserShortResponse toShortResponse(User user) {
        UserShortResponse dto = new UserShortResponse();
        dto.setId(user.getId());
        dto.setDisplayName(user.getDisplayName());
        dto.setUserTag(user.getUserTag());
        return dto;
    }
}