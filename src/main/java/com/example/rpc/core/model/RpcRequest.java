package com.example.rpc.core.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>RpcRequest 是一个用于封装 RPC 请求的模型类。</p>
 * <p>它实现了 Serializable 接口以支持序列化传输。</p>
 * 
 * <p>字段说明：</p>
 * <ul>
 *     <li><strong>requestId</strong>: 唯一标识一个 RPC 请求的 UUID。</li>
 *     <li><strong>serviceName</strong>: 目标服务的名称。</li>
 *     <li><strong>methodName</strong>: 调用的目标方法名称。</li>
 *     <li><strong>parameterTypes</strong>: 方法参数的类型数组。</li>
 *     <li><strong>parameters</strong>: 方法参数的值数组。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * &#064;date  2025/4/8
 */
public class RpcRequest implements Serializable {
    private UUID requestId;
    private String serviceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public RpcRequest() {}
    /**
     * 构造函数，用于初始化 RpcRequest 对象。
     *
     * @param requestId      唯一标识请求的 UUID。
     * @param serviceName    目标服务的名称。
     * @param methodName     调用的目标方法名称。
     * @param parameterTypes 方法参数的类型数组。
     * @param parameters     方法参数的值数组。
     */
    public RpcRequest(UUID requestId, String serviceName, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    /**
     * 获取请求的唯一标识符。
     *
     * @return 请求的 UUID。
     */
    public UUID getRequestId() {
        return requestId;
    }

    /**
     * 设置请求的唯一标识符。
     *
     * @param requestId 请求的 UUID。
     */
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    /**
     * 获取目标服务的名称。
     *
     * @return 服务名称。
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 设置目标服务的名称。
     *
     * @param serviceName 服务名称。
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 获取调用的目标方法名称。
     *
     * @return 方法名称。
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * 设置调用的目标方法名称。
     *
     * @param methodName 方法名称。
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 获取方法参数的类型数组。
     *
     * @return 参数类型数组。
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * 设置方法参数的类型数组。
     *
     * @param parameterTypes 参数类型数组。
     */
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * 获取方法参数的值数组。
     *
     * @return 参数值数组。
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * 设置方法参数的值数组。
     *
     * @param parameters 参数值数组。
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}