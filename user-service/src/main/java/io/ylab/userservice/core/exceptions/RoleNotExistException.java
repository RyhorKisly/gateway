package io.ylab.userservice.core.exceptions;

public class RoleNotExistException extends UserServiceException{
    public RoleNotExistException(String message) {
        super(message);
    }
}
