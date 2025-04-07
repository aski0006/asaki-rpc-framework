package com.example.rpc.core.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * RpcResponse 是一个用于封装 RPC 响应的模型类。
 * 它实现了 Serializable 接口以支持序列化传输。
 * <br></br>
 * 字段说明：
 * <p> - requestId: 唯一标识一个 RPC 请求的 UUID，与请求中的 requestId 对应。</p>
 * <p> - result: 方法调用的结果，如果调用成功则包含返回值，否则为 null。</p>
 * <p> - exception: 方法调用过程中抛出的异常，如果调用失败则包含异常信息，否则为 null。</p>
 */
public class RpcResponse implements Serializable {
    private UUID requestId;
    private Object result;
    private Throwable exception;


    public RpcResponse(){}

    /**
     * 构造函数，用于初始化 RpcResponse 对象。
     *
     * @param requestId 唯一标识请求的 UUID。
     * @param result    方法调用的结果。
     * @param exception 方法调用过程中抛出的异常。
     */
    public RpcResponse(UUID requestId, Object result, Throwable exception) {
        this.requestId = requestId;
        this.result = result;
        this.exception = exception;
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
     * 获取方法调用的结果。
     *
     * @return 方法调用的结果。
     */
    public Object getResult() {
        return result;
    }

    /**
     * 设置方法调用的结果。
     *
     * @param result 方法调用的结果。
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 获取方法调用过程中抛出的异常。
     *
     * @return 方法调用的异常。
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * 设置方法调用过程中抛出的异常。
     *
     * @param exception 方法调用的异常。
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }
}