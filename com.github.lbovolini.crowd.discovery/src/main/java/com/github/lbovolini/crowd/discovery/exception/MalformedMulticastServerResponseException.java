package com.github.lbovolini.crowd.discovery.exception;

public class MalformedMulticastServerResponseException extends RuntimeException {

    public MalformedMulticastServerResponseException(String message) {
        super(message);
    }

    public MalformedMulticastServerResponseException(Throwable cause) {
        super(cause);
    }

    public MalformedMulticastServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
