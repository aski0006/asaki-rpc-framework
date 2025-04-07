// RpcClientException.java
package com.example.rpc.client.exception;

public class RpcClientException extends RuntimeException {
    public RpcClientException(String message) {
        super(message);
    }

    public RpcClientException(String message, Throwable cause) {
        super(message, cause);
    }
}