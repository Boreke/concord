package org.concord.backend.exceptions;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }
}
