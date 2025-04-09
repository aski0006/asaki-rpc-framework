package com.example.rpc.client.handler;

import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>ClientResponseHandler 类的主要作用是处理从服务器接收到的 RPC 响应。</p>
 * <p>该类继承自 {@link SimpleChannelInboundHandler}，专门用于处理 {@link RpcResponse} 类型的消息。</p>
 * <p>核心功能详细说明：</p>
 * <p>1. 维护请求与响应的映射关系：使用 {@link ConcurrentHashMap} 存储请求 ID 和对应的 {@link CompletableFuture}。</p>
 * <p>2. 处理响应消息：在 {@link #channelRead0(ChannelHandlerContext, RpcResponse)} 方法中，根据响应的请求 ID 获取对应的 {@link CompletableFuture}，并完成该 {@link CompletableFuture}。</p>
 *
 * <p>使用示例：</p>
 * <p>以下代码展示了如何创建 ClientResponseHandler 实例并处理响应消息。</p>
 * <pre>
 * ClientResponseHandler handler = new ClientResponseHandler();
 * ChannelHandlerContext ctx = ...; // 假设已经获取了 ChannelHandlerContext 实例
 * RpcResponse response = ...; // 假设已经获取了 RpcResponse 实例
 * handler.channelRead0(ctx, response);
 * </pre>
 *
 * <p>构造函数说明：</p>
 * <p>该类的构造函数不接受任何参数，可直接使用无参构造函数创建实例。</p>
 *
 * <p>使用限制与潜在副作用：</p>
 * <p>1. 该类必须在 Netty 的 ChannelPipeline 中使用才能生效。因为其核心功能是处理接收到的 RPC 响应消息。</p>
 * <p>2. 传入的 {@link ChannelHandlerContext} 和 {@link RpcResponse} 参数必须有效，否则可能会导致异常。</p>
 * @Author 郑钦 (Asaki0019)
 * @Date 2025/4/8
 */
public class ClientResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    // 使用 ConcurrentHashMap 来存储请求 ID 和对应的 CompletableFuture
   public static Map<String, CompletableFuture<RpcResponse>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        // 根据响应的请求 ID 获取对应的 CompletableFuture
        CompletableFuture<RpcResponse> future = pendingRequests.remove(rpcResponse.getRequestId().toString());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            System.err.println("No pending request found for response ID: " + rpcResponse.getRequestId());
        }
    }

}