package org.concord.backend.config.constants;

import org.springframework.security.core.GrantedAuthority;

public interface Roles extends GrantedAuthority {
    String USER = "USER";
    String ADMIN = "ADMIN";
    String SUPER_ADMIN = "SUPER_ADMIN";
}
