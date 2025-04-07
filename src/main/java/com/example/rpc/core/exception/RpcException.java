package com.example.rpc.core.exception;

public class RpcException extends RuntimeException{
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(String message) {
        super(message);
    }
}