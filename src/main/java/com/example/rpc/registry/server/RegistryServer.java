package com.example.rpc.registry.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p>RegistryServer 是注册中心服务器的实现类。</p>
 * <p>该类负责启动和管理基于 Netty 的注册中心服务器，处理服务注册、注销和发现等请求。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>启动服务器</strong>: 使用 Netty 框架启动注册中心服务器，监听指定端口。</li>
 *     <li><strong>配置 TCP 参数</strong>: 配置服务器的 TCP 参数，如 SO_BACKLOG、SO_REUSEADDR 等。</li>
 *     <li><strong>优雅关闭</strong>: 提供优雅关闭服务器的功能，确保资源正确释放。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RegistryServer {
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public RegistryServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        System.out.println("Registry server started on port: " + port);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new RegistryChannelInitializer())
                    // 增加TCP参数配置
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        System.out.println("Registry server stopped");
    }
}