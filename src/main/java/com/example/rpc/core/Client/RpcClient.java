package com.example.rpc.core.Client;

import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;

/**
 * <p>RpcClient 是 RPC 客户端的核心接口。</p>
 * <p>该接口定义了发送 RPC 请求的方法，所有 RPC 客户端实现类都需要实现该接口。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>发送 RPC 请求</strong>: 向 RPC 服务器发送请求，并返回服务器的响应。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}