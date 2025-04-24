package com.example.rpc.server.handler;

import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import com.example.rpc.server.server.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * <p>ServerRequestHandler 是 Netty 服务器端的请求处理器。</p>
 * <p>该处理器负责处理来自客户端的 RPC 请求，并将结果返回给客户端。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>处理 RPC 请求</strong>: 从请求中提取服务名称、方法名称和参数，调用相应的方法，并返回结果。</li>
 *     <li><strong>异常处理</strong>: 捕获并处理在处理请求过程中发生的异常，将异常信息返回给客户端。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class ServerRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * <p>处理接收到的 RPC 请求。</p>
     * <p>该方法从请求中提取服务名称、方法名称和参数，调用相应的方法，并将结果封装在 RpcResponse 中返回给客户端。</p>
     *
     * @param ctx     通道处理上下文
     * @param request 接收到的 RPC 请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            System.out.println("Received request: " + request);
            Object service = ServiceRegistry.getService(request.getServiceName());
            System.out.println("Service found: " + service);
            Method method = service.getClass().getMethod(
                    request.getMethodName(),
                    request.getParameterTypes()
            );
            System.out.println("Method found: " + method);
            Object result = method.invoke(service, request.getParameters());
            System.out.println("Method executed, result: " + result);
            response.setResult(result);
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
            response.setException(e);
        }

        ctx.writeAndFlush(response);
    }

    /**
     * <p>捕获并处理通道中的异常。</p>
     * <p>当通道中发生异常时，打印异常堆栈信息，并关闭通道。</p>
     *
     * @param ctx   通道处理上下文
     * @param cause 异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}