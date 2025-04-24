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
 * <p>使用示例：</p>
 * <pre>
 * NettyRpcClient client = new NettyRpcClient();
 * RpcRequest request = new RpcRequest(...);
 * CompletableFuture&lt;RpcResponse&gt; responseFuture = client.sendRequest(request, "localhost", 8080);
 * RpcResponse response = responseFuture.get(); // 阻塞等待响应
 * client.shutdown();
 * </pre>
 * <p>构造函数参数：</p>
 * <ul>
 *     <li>无参数构造函数：初始化 NettyRpcClient 实例，配置客户端相关参数。</li>
 * </ul>
 * <p>特殊使用限制或潜在的副作用：</p>
 * <ul>
 *     <li>确保在调用 `sendRequest` 方法之前，NettyRpcClient 实例已经被正确初始化。</li>
 *     <li>在应用程序结束时，应调用 `shutdown` 方法以释放资源。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * &#064;date  2025/4/8
 */
public class NettyRpcClient {
    // 定义 EventLoopGroup 用于处理 I/O 操作
    private final EventLoopGroup group = new NioEventLoopGroup();
    // 定义 Bootstrap 用于配置和启动客户端
    private final Bootstrap bootstrap = new Bootstrap();

    /**
     * 构造函数：初始化 NettyRpcClient 实例，配置客户端相关参数。
     */
    public NettyRpcClient() {
        // 配置 Bootstrap
        bootstrap.group(group)
                .channel(NioSocketChannel.class) // 设置 Channel 类型为 NioSocketChannel
                .handler(new ClientChannelInitializer()) // 设置 Channel 初始化处理器
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ClientConfig.getRequestTimeout()); // 设置连接超时时间
    }

    /**
     * 发送 RPC 请求到指定的服务器地址和端口。
     *
     * @param request RPC 请求对象
     * @param host    服务器地址
     * @param port    服务器端口
     * @return 一个 CompletableFuture 对象，用于异步获取 RPC 响应
     */
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request, String host, int port) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        // 连接到服务器并发送请求
        bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 连接成功，发送请求
                future.channel().writeAndFlush(request).addListener(f -> {
                    if (!f.isSuccess()) {
                        // 发送请求失败，完成异常
                        resultFuture.completeExceptionally(f.cause());
                    }
                });
            } else {
                // 连接失败，完成异常
                resultFuture.completeExceptionally(future.cause());
            }
        });

        return resultFuture;
    }

    /**
     * 关闭客户端，释放资源。
     */
    public void shutdown() {
        group.shutdownGracefully();
    }
}