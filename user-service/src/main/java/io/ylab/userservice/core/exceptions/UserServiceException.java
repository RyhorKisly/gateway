package io.ylab.userservice.core.exceptions;

/**
 * Common class for all exceptions created in this application
 */
public class UserServiceException extends RuntimeException{
    public UserServiceException(String message) {
        super(message);
    }
}
