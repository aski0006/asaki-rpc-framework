package com.example.rpc.core.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>RpcResponse 是一个用于封装 RPC 响应的模型类。</p>
 * <p>它实现了 Serializable 接口以支持序列化传输。</p>
 * 
 * <p>字段说明：</p>
 * <ul>
 *     <li><strong>requestId</strong>: 唯一标识一个 RPC 请求的 UUID，与请求中的 requestId 对应。</li>
 *     <li><strong>result</strong>: 方法调用的结果，如果调用成功则包含返回值，否则为 null。</li>
 *     <li><strong>exception</strong>: 方法调用过程中抛出的异常，如果调用失败则包含异常信息，否则为 null。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RpcResponse implements Serializable {
    private UUID requestId;
    private Object result;
    private Throwable exception;


    public RpcResponse(){}

    /**
     * <p>构造函数，用于初始化 RpcResponse 对象。</p>
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
     * <p>获取请求的唯一标识符。</p>
     *
     * @return 请求的 UUID。
     */
    public UUID getRequestId() {
        return requestId;
    }

    /**
     * <p>设置请求的唯一标识符。</p>
     *
     * @param requestId 请求的 UUID。
     */
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    /**
     * <p>获取方法调用的结果。</p>
     *
     * @return 方法调用的结果。
     */
    public Object getResult() {
        return result;
    }

    /**
     * <p>设置方法调用的结果。</p>
     *
     * @param result 方法调用的结果。
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * <p>获取方法调用过程中抛出的异常。</p>
     *
     * @return 方法调用的异常。
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * <p>设置方法调用过程中抛出的异常。</p>
     *
     * @param exception 方法调用的异常。
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }
}