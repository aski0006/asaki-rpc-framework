package com.example.rpc.core.server;

public interface RpcServer {
    void start();
    void stop();
    void registerService(String serviceName, Object serviceImpl);
}