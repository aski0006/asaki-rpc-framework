package com.example.rpc.registry.handler;

import com.example.rpc.registry.model.ServiceInstance;
import com.example.rpc.registry.service.RegistryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistryRequestHandlerTest {

    private RegistryRequestHandler handler;
    private RegistryService registryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射注入mock的RegistryService
        registryService = mock(RegistryService.class);
        handler = new RegistryRequestHandler();
        Field registryServiceField = RegistryRequestHandler.class.getDeclaredField("registryService");
        registryServiceField.setAccessible(true);
        registryServiceField.set(handler, registryService);
    }

    @Test
    void handleRegisterRequest_ShouldRegisterServiceAndReturnOk() throws Exception {
        // 模拟请求
        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.uri()).thenReturn("/register");
        when(request.method()).thenReturn(HttpMethod.POST);
        String jsonContent = "{\"serviceName\":\"testService\", \"host\":\"localhost\", \"port\":8080}";
        ByteBuf content = Unpooled.copiedBuffer(jsonContent, CharsetUtil.UTF_8);
        when(request.content()).thenReturn(content);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        // 执行处理
        handler.channelRead0(ctx, request);

        // 验证服务注册
        ArgumentCaptor<ServiceInstance> captor = ArgumentCaptor.forClass(ServiceInstance.class);
        verify(registryService).register(captor.capture());

        ServiceInstance instance = captor.getValue();
        assertEquals("testService", instance.getServiceName());
        assertEquals("localhost", instance.getHost());
        assertEquals(8080, instance.getPort());

        // 验证响应
        ArgumentCaptor<FullHttpResponse> responseCaptor = ArgumentCaptor.forClass(FullHttpResponse.class);
        verify(ctx).writeAndFlush(responseCaptor.capture());

        FullHttpResponse response = responseCaptor.getValue();
        assertEquals(HttpResponseStatus.OK, response.status());
        assertEquals("application/json", response.headers().get(HttpHeaderNames.CONTENT_TYPE));
        assertEquals("{\"status\":\"registered\"}",
                response.content().toString(CharsetUtil.UTF_8));
    }

    @Test
    void handleDiscoverRequest_ShouldReturnServiceInstances() throws Exception {
        // 模拟请求参数
        String serviceName = "testService";
        ServiceInstance instance = new ServiceInstance();
        when(registryService.discoveryServiceInstance(serviceName))
                .thenReturn(Collections.singletonList(instance));

        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.uri()).thenReturn("/discover?service=" + serviceName);
        when(request.method()).thenReturn(HttpMethod.GET);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        // 执行处理
        handler.channelRead0(ctx, request);

        // 验证响应
        ArgumentCaptor<FullHttpResponse> responseCaptor = ArgumentCaptor.forClass(FullHttpResponse.class);
        verify(ctx).writeAndFlush(responseCaptor.capture());

        FullHttpResponse response = responseCaptor.getValue();
        assertEquals(HttpResponseStatus.OK, response.status());
        String expectedJson = objectMapper.writeValueAsString(Collections.singletonList(instance));
        assertEquals(expectedJson, response.content().toString(CharsetUtil.UTF_8));
    }

    @Test
    void handleDiscoverRequest_MissingServiceParam_ShouldReturnBadRequest() {
        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.uri()).thenReturn("/discover");
        when(request.method()).thenReturn(HttpMethod.GET);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        handler.channelRead0(ctx, request);

        ArgumentCaptor<FullHttpResponse> responseCaptor = ArgumentCaptor.forClass(FullHttpResponse.class);
        verify(ctx).writeAndFlush(responseCaptor.capture());

        assertEquals(HttpResponseStatus.BAD_REQUEST, responseCaptor.getValue().status());
    }

    @Test
    void handleUnknownPath_ShouldReturnNotFound() {
        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.uri()).thenReturn("/unknown");
        when(request.method()).thenReturn(HttpMethod.POST);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        handler.channelRead0(ctx, request);

        ArgumentCaptor<FullHttpResponse> responseCaptor = ArgumentCaptor.forClass(FullHttpResponse.class);
        verify(ctx).writeAndFlush(responseCaptor.capture());

        assertEquals(HttpResponseStatus.NOT_FOUND, responseCaptor.getValue().status());
    }

    @Test
    void handleInvalidJson_ShouldReturnInternalError() {
        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.uri()).thenReturn("/register");
        when(request.method()).thenReturn(HttpMethod.POST);
        ByteBuf invalidContent = Unpooled.copiedBuffer("invalid json", CharsetUtil.UTF_8);
        when(request.content()).thenReturn(invalidContent);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        handler.channelRead0(ctx, request);

        ArgumentCaptor<FullHttpResponse> responseCaptor = ArgumentCaptor.forClass(FullHttpResponse.class);
        verify(ctx).writeAndFlush(responseCaptor.capture());

        assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR, responseCaptor.getValue().status());
    }

    @Test
    void exceptionCaught_ShouldCloseContext() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Throwable cause = new RuntimeException("test error");

        handler.exceptionCaught(ctx, cause);

        verify(ctx).close();
    }
}