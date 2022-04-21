package com.finalproject.util.exception;

/**
 * Runtime exception that thrown in case new username already exists in database
 *
 */
public class UsernameNotUniqueException extends RuntimeException {

    public UsernameNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

}
