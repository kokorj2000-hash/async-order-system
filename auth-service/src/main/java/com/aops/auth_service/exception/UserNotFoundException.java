package com.aops.auth_service.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("User '" + username + "' not found");
    }

    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }

}