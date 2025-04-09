package com.example.rpc.registry;

import com.example.rpc.registry.config.RegistryConfig;
import com.example.rpc.registry.server.RegistryServer;

/**
 * <p>注册中心启动器</p>
 * <p>该类负责启动注册中心服务，提供两种启动方式：指定端口启动和使用配置启动。</p>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
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