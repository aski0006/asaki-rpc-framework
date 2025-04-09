// RpcClientProxy.java
package com.example.rpc.client.proxy;

import com.example.rpc.client.client.NettyRpcClient;
import com.example.rpc.client.discovery.ServiceDiscovery;
import com.example.rpc.client.discovery.impl.HttpServiceDiscovery;
import com.example.rpc.client.loadbalance.LoadBalancer;
import com.example.rpc.client.loadbalance.impl.RandomLoadBalancer;
import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import com.example.rpc.registry.model.ServiceInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.rpc.client.handler.ClientResponseHandler.pendingRequests;

/**
 * <p>RpcClientProxy 类的主要作用是作为 RPC 客户端的代理，用于调用远程服务。</p>
 * <p>该类实现了 {@link InvocationHandler} 接口，并通过动态代理机制来拦截方法调用，从而实现远程方法调用。</p>
 * <p>核心功能详细说明：</p>
 * <p>1. 服务发现：通过 {@link ServiceDiscovery} 接口的实现类（如 {@link HttpServiceDiscovery}）来发现服务实例。</p>
 * <p>2. 负载均衡：通过 {@link LoadBalancer} 接口的实现类（如 {@link RandomLoadBalancer}）来选择一个服务实例。</p>
 * <p>3. 发送请求：使用 {@link NettyRpcClient} 发送 RPC 请求到选定的服务实例。</p>
 * <p>4. 处理响应：接收并处理服务端返回的 RPC 响应，将响应结果返回给调用者。</p>
 *
 * <p>使用示例：</p>
 * <p>以下代码展示了如何创建 RpcClientProxy 实例并调用远程方法。</p>
 * <pre>
 * RpcClientProxy proxy = new RpcClientProxy(MyServiceInterface.class);
 * MyServiceInterface service = (MyServiceInterface) Proxy.newProxyInstance(
 *         MyServiceInterface.class.getClassLoader(),
 *         new Class<?>[]{MyServiceInterface.class},
 *         proxy
 * );
 * service.someMethod();
 * </pre>
 *
 * <p>构造函数说明：</p>
 * <p>该类的构造函数接受一个 Class 对象，表示要代理的服务接口。</p>
 *
 * <p>使用限制与潜在副作用：</p>
 * <p>1. 该类必须在动态代理环境中使用才能生效。因为其核心功能是拦截方法调用，若不在代理环境中，方法调用将不会被拦截。</p>
 * <p>2. 传入的服务接口必须有效，否则可能会导致异常。</p>
 *
 * @Author 郑钦 (Asaki0019)
 * @Date 2025/4/8
 */
public class RpcClientProxy implements InvocationHandler {
    private final ServiceDiscovery discovery = new HttpServiceDiscovery();
    private final LoadBalancer loadBalancer = new RandomLoadBalancer();
    private static final NettyRpcClient client = new NettyRpcClient();
    private final Class<?> serviceInterface;

    public RpcClientProxy(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Starting RPC invocation");
        List<ServiceInstance> instances = discovery.discover(serviceInterface.getName());
        System.out.println("Discovered service instances: " + instances);
        ServiceInstance instance = loadBalancer.select(instances);
        if (instance == null) {
            System.out.println("Service instance not found");
            return null;
        }
        System.out.println("Selected service instance: " + instance);
        RpcRequest request = new RpcRequest(
                java.util.UUID.randomUUID(),
                serviceInterface.getName(),
                method.getName(),
                method.getParameterTypes(),
                args
        );

        CompletableFuture<RpcResponse> future = client.sendRequest(request,
                instance.getHost(), instance.getPort());
        pendingRequests.put(request.getRequestId().toString(), future);
        System.out.println("Sent request to: " + instance.getHost() + ":" + instance.getPort());
        RpcResponse response = future.get();
        if (response.getException() != null) {
            System.out.println("Received exception from service: " + response.getException());
            throw response.getException();
        }
        System.out.println("Received response: " + response.getResult());
        return response.getResult();
    }

    public static void shutdown() {
        client.shutdown();
    }
}