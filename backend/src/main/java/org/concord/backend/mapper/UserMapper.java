package org.concord.backend.mapper;

import org.concord.backend.dal.model.enums.Role;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dto.request.UserRequest;
import org.concord.backend.dto.response.UserResponse;

public class UserMapper {
    public static User toEntity(UserRequest dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setUserTag(dto.getUserTag());
        user.setFullName(dto.getFullName());
        user.setRole(Role.valueOf(dto.getRole()));
        user.setVerified(dto.getIsVerified());
        return user;
    }

    public static UserResponse toDto(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUserTag(user.getUserTag());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole().name());
        dto.setIsVerified(user.isVerified());
        return dto;
    }
}