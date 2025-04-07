package com.example.rpc.core.Client;

import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
