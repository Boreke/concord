package org.concord.backend.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileDTO(
        @Size(max = 50) String displayName,
        @Size(max = 255) String bio
) {}