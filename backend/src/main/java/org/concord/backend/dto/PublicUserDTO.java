package org.concord.backend.dto;

public record PublicUserDTO(
        String username,
        String displayName,
        String bio
) {}