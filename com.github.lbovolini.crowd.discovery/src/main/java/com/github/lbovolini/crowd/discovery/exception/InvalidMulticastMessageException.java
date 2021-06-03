package com.github.lbovolini.crowd.discovery.exception;

public class InvalidMulticastMessageException extends RuntimeException {

    public InvalidMulticastMessageException(String message) {
        super(message);
    }

    public InvalidMulticastMessageException(Throwable cause) {
        super(cause);
    }

    public InvalidMulticastMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
