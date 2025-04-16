package com.example.rpc.registry;

import com.example.rpc.registry.config.RegistryConfig;
import com.example.rpc.registry.server.RegistryServer;
import io.netty.channel.ChannelFuture;

/**
 * <p>注册中心启动器</p>
 * <p>该类负责启动注册中心服务，提供两种启动方式：指定端口启动和使用配置启动。</p>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RpcRegistryStarter {
    public static void start(int port) {
        try {
            RegistryServer server = new RegistryServer(port);
            ChannelFuture future = server.start();
            future.channel().closeFuture().sync(); // 阻塞主线程
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void startWithConfig() {
        start(RegistryConfig.getPort());
    }

    public static void main(String[] args) {
        startWithConfig();
    }
}