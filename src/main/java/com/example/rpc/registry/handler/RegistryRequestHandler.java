package com.example.rpc.registry.handler;

import com.example.rpc.registry.model.ServiceInstance;
import com.example.rpc.registry.service.MemoryRegistryService;
import com.example.rpc.registry.service.RegistryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import static io.netty.buffer.Unpooled.*;

/**
 * RegistryRequestHandler 是一个处理注册和发现服务请求的 Netty 处理器。
 * 它继承自 SimpleChannelInboundHandler 并专门处理 FullHttpRequest 类型的消息。
 * 主要功能包括：
 * <p>- 处理服务注册请求 (/register)。</p>
 * <p>- 处理服务发现请求 (/discover)。</p>
 * <p>- 发送错误响应。</p>
 */
public class RegistryRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final RegistryService registryService = new MemoryRegistryService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理注册和发现服务请求。
     *
     * @param ctx     通道上下文。
     * @param request HTTP 请求。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 获取请求的URI和HTTP方法
        String path = request.uri();
        HttpMethod method = request.method();

        try {
            // 如果请求方法是POST且路径以"/register"开头，则处理注册请求
            if (method == HttpMethod.POST && path.startsWith("/register")) {
                System.out.println("Begin to register service");
                handleRegister(ctx, request);
            // 如果请求方法是GET且路径以"/discover"开头，则处理发现请求
            } else if (method == HttpMethod.GET && path.startsWith("/discover")) {
                System.out.println("Begin to discover service");
                handleDiscover(ctx, path);
            // 如果请求不匹配以上两种情况，则返回 404 错误
            } else {
                System.out.println("Invalid request");
                sendError(ctx, HttpResponseStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // 如果发生异常，则返回 500 错误
            System.out.println("Exception occurred: " + e.getMessage());
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 发送错误响应。
     *
     * @param ctx    通道上下文。
     * @param status HTTP 响应状态。
     */
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        String errorJson = "{\"error\":\"" + status + "\"}";
        extractErrorMsg(ctx, status, errorJson);
    }

    /**
     * 解析服务名称。
     *
     * @param path 请求路径。
     * @return 服务名称，如果未找到则返回 null。
     */
    private String extractServiceName(String path) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(path);
        Map<String, List<String>> params = queryDecoder.parameters();
        List<String> services = params.get("service");
        return (services != null && !services.isEmpty()) ? services.get(0) : null;
    }

    /**
     * 处理服务注册请求。
     *
     * @param ctx     通道上下文。
     * @param request HTTP 请求。
     * @throws Exception 如果解析或注册过程中发生异常。
     */
    private void handleRegister(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        try{
            String json = request.content().toString(CharsetUtil.UTF_8);
            System.out.println("Received registration: " + json);
            ByteBuf content = request.content();
            ServiceInstance instance = objectMapper.readValue(
                    content.toString(CharsetUtil.UTF_8),
                    ServiceInstance.class
            );
            if(instance == null) {
                System.out.println("Service instance is null");
                return;
            }
            // 注册服务实例
            instance.setLastUpdateTimestamp(System.currentTimeMillis());
            registryService.register(instance);
            sendResponse(ctx, HttpResponseStatus.OK, "{\"status\":\"registered\"}");
        }catch (Exception e){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
        }
    }

    /**
     * 处理服务发现请求。
     *
     * @param ctx 通道上下文。
     * @param path 请求路径。
     * @throws Exception 如果解析或发现过程中发生异常。
     */
    private void handleDiscover(ChannelHandlerContext ctx, String path)
            throws Exception {
        System.out.println("开始处理服务发现请求，路径：" + path);
        String serviceName = extractServiceName(path);
        System.out.println("提取的服务名称：" + serviceName);
        if (serviceName == null) {
            System.out.println("服务名称为空，发送错误响应");
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        System.out.println("开始发现服务实例，服务名称：" + serviceName);
        List<ServiceInstance> instances = registryService.discoveryServiceInstance(serviceName);
        System.out.println("发现的服务实例数量：" + instances.size());
        sendResponse(ctx, HttpResponseStatus.OK, toJson(instances));
    }

    /**
     * 将服务实例列表转换为 JSON 字符串。
     *
     * @param instances 服务实例列表。
     * @return JSON 字符串。
     */
    private String toJson(List<ServiceInstance> instances) {
        try {
            return objectMapper.writeValueAsString(instances);
        } catch (JsonProcessingException e) {
            return "[]"; // 序列化失败返回空数组
        }
    }

    /**
     * 发送正常响应。
     *
     * @param ctx     通道上下文。
     * @param status  HTTP 响应状态。
     * @param content 响应内容。
     */
    private void sendResponse(ChannelHandlerContext ctx, HttpResponseStatus status,
                              String content) {
        extractErrorMsg(ctx, status, content);
    }

    /**
     * 从给定的ChannelHandlerContext、HttpResponseStatus和内容字符串中提取错误信息，并构建一个包含错误信息的HTTP响应。
     *
     * @param ctx ChannelHandlerContext，用于发送HTTP响应
     * @param status HttpResponseStatus，HTTP响应的状态码
     * @param content 错误信息的内容
     */
    private void extractErrorMsg(ChannelHandlerContext ctx, HttpResponseStatus status, String content) {
        // 将错误信息内容转换为UTF-8编码的ByteBuf
        ByteBuf buf = copiedBuffer(content, CharsetUtil.UTF_8);
        // 创建一个包含错误信息的FullHttpResponse
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, buf);
        // 设置响应头Content-Type为application/json
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        // 设置响应头Content-Length为错误信息内容的字节数
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        // 发送HTTP响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}