package com.example.rpc.registry;

import com.example.rpc.registry.config.RegistryConfig;
import com.example.rpc.registry.server.RegistryServer;

/**
 *  @Author: Asaki0019
 *  @Description: 注册中心启动器
 */
public class RegistryStarter {
    public static void start(int port) {
        try {
            new RegistryServer(port).start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void startWithConfig() {
        start(RegistryConfig.getPort());
    }

    public static void main(String[] args) {
        startWithConfig();
    }
}