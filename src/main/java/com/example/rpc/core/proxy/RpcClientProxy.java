package com.example.rpc.core.proxy;

import com.example.rpc.core.Client.RpcClient;
import com.example.rpc.core.model.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * <p>RpcClientProxy 是一个动态代理类，用于实现 RPC 调用。</p>
 * <p>它通过拦截目标方法调用，将方法信息封装为 RpcRequest 并发送给 RpcClient。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>拦截方法调用</strong>: 拦截目标方法调用，并将其封装为 RpcRequest。</li>
 *     <li><strong>发送 RPC 请求</strong>: 将封装好的 RpcRequest 发送给 RpcClient。</li>
 *     <li><strong>处理 Object 方法</strong>: 处理 Object 类中的方法调用，如 equals、hashCode 和 toString。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final String serviceName;

    /**
     * 构造函数，初始化 RpcClientProxy。
     *
     * @param rpcClient  RPC 客户端，用于发送请求。
     * @param serviceName 服务名称，用于标识目标服务。
     */
    public RpcClientProxy(RpcClient rpcClient, String serviceName) {
        this.rpcClient = rpcClient;
        this.serviceName = serviceName;
    }

    /**
     * 拦截目标方法调用，处理 RPC 请求。
     *
     * @param proxy 代理对象。
     * @param method 被调用的方法。
     * @param args 方法参数。
     * @return 方法调用的结果。
     * @throws Throwable 如果调用过程中发生异常。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return handleObjectMethod(proxy, method, args);
        }
        RpcRequest request = new RpcRequest(
                UUID.randomUUID(),
                serviceName,
                method.getName(),
                method.getParameterTypes(),
                args
        );
        return rpcClient.sendRequest(request);
    }

    /**
     * 处理 Object 类中的方法调用（如 equals、hashCode、toString）。
     *
     * @param proxy 代理对象。
     * @param method 被调用的方法。
     * @param args 方法参数。
     * @return 方法调用的结果。
     */
    private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "equals":
                return proxy == args[0]; // 简化实现，实际可能需要更精确比较
            case "hashCode":
                return System.identityHashCode(proxy);
            case "toString":
                return "RPC Proxy for " + serviceName;
            default:
                throw new UnsupportedOperationException("Method not supported: " + method);
        }
    }

}