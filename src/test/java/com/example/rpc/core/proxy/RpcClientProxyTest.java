package com.example.rpc.core.proxy;


import com.example.rpc.core.Client.RpcClient;
import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * {@code RpcClientProxyTest} 类的主要用途是对 {@link RpcClientProxy} 类的各项功能进行单元测试。
 * 本测试类借助 Mockito 框架来模拟 {@link RpcClient} 的行为，进而全面验证 {@code RpcClientProxy} 类的正确性和可靠性。
 *
 * <h2>核心测试功能</h2>
 * <ol>
 *   <li><strong>模拟 RpcClient 行为</strong>：利用 Mockito 框架模拟 {@code RpcClient} 的各种行为，为测试提供可控的环境。</li>
 *   <li><strong>验证方法调用</strong>：检验 {@code RpcClientProxy} 的 {@code invoke} 方法是否能正确调用 {@code RpcClient} 的 {@code sendRequest} 方法，确保方法调用逻辑的正确性。</li>
 *   <li><strong>验证返回结果</strong>：确认 {@code RpcClientProxy} 的 {@code invoke} 方法是否能返回预期的正确结果，保证功能的准确性。</li>
 * </ol>
 *
 * <h2>使用示例</h2>
 * 以下代码展示了如何使用 {@code RpcClientProxyTest} 类进行测试：
 * <pre>
 * {@code
 * RpcClientProxyTest test = new RpcClientProxyTest();
 * test.setUp();
 * test.invoke_ObjectMethodEquals_ReturnsCorrectResult();
 * }
 * </pre>
 *
 * <h2>构造函数参数</h2>
 * <ul>
 *   <li><strong>rpcClient</strong>：类型为 {@link RpcClient}，用于模拟的 {@code RpcClient} 对象，为测试提供模拟的客户端行为。</li>
 *   <li><strong>serviceName</strong>：类型为 {@link String}，服务名称，用于生成 {@link RpcRequest} 对象，标识要调用的服务。</li>
 * </ul>
 *
 * <h2>使用限制和潜在副作用</h2>
 * <ul>
 *   <li><strong>使用场景限制</strong>：该类仅适用于单元测试，请勿在生产环境中使用，以避免引入不必要的测试逻辑和潜在风险。</li>
 *   <li><strong>框架依赖要求</strong>：需要确保 Mockito 框架已正确配置到项目中，否则测试将无法正常运行。</li>
 * </ul>
 */
public class RpcClientProxyTest {

    @Mock
    private RpcClient rpcClient;

    @InjectMocks
    private RpcClientProxy rpcClientProxy;

    private static final String SERVICE_NAME = "testService";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rpcClientProxy = new RpcClientProxy(rpcClient, SERVICE_NAME);
    }

    @Test
    public void invoke_ObjectMethodEquals_ReturnsCorrectResult() throws Throwable {
        Object proxy = new Object();
        Method method = Object.class.getMethod("equals", Object.class);
        Object[] args = {proxy};

        Object result = rpcClientProxy.invoke(proxy, method, args);

        assertTrue((Boolean) result);
    }

    @Test
    public void invoke_ObjectMethodHashCode_ReturnsCorrectResult() throws Throwable {
        Object proxy = new Object();
        Method method = Object.class.getMethod("hashCode");

        Object result = rpcClientProxy.invoke(proxy, method, null);

        assertEquals(System.identityHashCode(proxy), result);
    }

    @Test
    public void invoke_ObjectMethodToString_ReturnsCorrectResult() throws Throwable {
        Object proxy = new Object();
        Method method = Object.class.getMethod("toString");

        Object result = rpcClientProxy.invoke(proxy, method, null);

        assertEquals("RPC Proxy for " + SERVICE_NAME, result);
    }

    @Test
    public void invoke_CustomMethod_SendsRpcRequest() throws Throwable {
        Object proxy = new Object();
        Method method = String.class.getMethod("toUpperCase");
        Object[] args = null;

        RpcResponse mockResponse = new RpcResponse(UUID.randomUUID(), "UPPERCASE", null);
        when(rpcClient.sendRequest(any(RpcRequest.class))).thenReturn(mockResponse);

        Object result = rpcClientProxy.invoke(proxy, method, args);

        ArgumentCaptor<RpcRequest> requestCaptor = ArgumentCaptor.forClass(RpcRequest.class);
        verify(rpcClient).sendRequest(requestCaptor.capture());

        RpcRequest capturedRequest = requestCaptor.getValue();
        assertEquals(SERVICE_NAME, capturedRequest.getServiceName());
        assertEquals("toUpperCase", capturedRequest.getMethodName());
    }
}