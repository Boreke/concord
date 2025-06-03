package org.concord.backend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenInvalidException extends AuthenticationException {
    public JwtTokenInvalidException(String msg) {
        super(msg);
    }
}