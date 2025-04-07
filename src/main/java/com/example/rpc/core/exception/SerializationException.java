package com.example.rpc.core.exception;

public class SerializationException extends RuntimeException{
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

