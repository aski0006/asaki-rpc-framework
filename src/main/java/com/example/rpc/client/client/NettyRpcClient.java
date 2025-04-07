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