
package com.example.rpc.server;

import com.example.rpc.server.config.ServerConfig;
import com.example.rpc.server.server.NettyServer;
import com.example.rpc.server.server.ServiceRegistry;
import io.netty.channel.ChannelFuture;

/**
 * <p>RPC 服务器启动类</p>
 * <p>该类负责启动 RPC 服务，包括服务注册轮询和 Netty 服务器的启动。</p>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RpcServerStarter {

    /**
     * <p>启动 RPC 服务</p>
     * <p>该方法会启动服务注册轮询，并启动 Netty 服务器。</p>
     *
     * @throws Exception
     */
    public static void start() throws Exception {
        // 启动服务注册轮询
        ServiceRegistry.startPolling();
        // 注册 JVM 关闭钩子，在 JVM 关闭时停止服务注册轮询
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ServiceRegistry.stopPolling();
            System.out.println("Server shutdown gracefully");
        }));
        // 启动 Netty 服务器
        NettyServer server = new NettyServer(ServerConfig.getPort());
        ChannelFuture future = server.start();
        future.channel().closeFuture().sync(); // 阻塞主线程
    }

    public static void main(String[] args) throws Exception {
        // 启动 RPC 服务
        start();
    }
}