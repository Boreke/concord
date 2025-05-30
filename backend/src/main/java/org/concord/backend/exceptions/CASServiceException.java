package org.concord.backend.exceptions;


import org.concord.backend.exceptions.http.HttpInternalServerErrorException;

public class CASServiceException extends HttpInternalServerErrorException {
    public CASServiceException(String message) {
      super(message);
    }

    public CASServiceException(String message, Throwable cause) {
      super(message, cause);
    }
}
