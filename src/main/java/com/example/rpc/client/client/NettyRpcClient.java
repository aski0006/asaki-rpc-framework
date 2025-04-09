package com.example.rpc.client.client;

import com.example.rpc.client.config.ClientConfig;
import com.example.rpc.client.handler.ClientChannelInitializer;
import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.CompletableFuture;

/**
 * <p>NettyRpcClient 是一个基于 Netty 的 RPC 客户端实现类。</p>
 * <p>该类负责与 RPC 服务器建立连接，并发送 RPC 请求。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>初始化 Netty 客户端</strong>: 使用 Bootstrap 配置 Netty 客户端，包括 EventLoopGroup、Channel 类型和处理器。</li>
 *     <li><strong>发送 RPC 请求</strong>: 提供 `sendRequest` 方法，用于向指定的服务器地址和端口发送 RPC 请求，并返回一个 CompletableFuture 对象。</li>
 *     <li><strong>关闭客户端</strong>: 提供 `shutdown` 方法，用于优雅地关闭客户端资源。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class NettyRpcClient {
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();

    public NettyRpcClient() {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ClientConfig.getRequestTimeout());
    }

    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request, String host, int port) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                future.channel().writeAndFlush(request).addListener(f -> {
                    if (!f.isSuccess()) {
                        resultFuture.completeExceptionally(f.cause());
                    }
                });
            } else {
                resultFuture.completeExceptionally(future.cause());
            }
        });

        return resultFuture;
    }

    public void shutdown() {
        group.shutdownGracefully();
    }
}