package com.example.rpc.server.function.impl;

import com.example.rpc.server.annotation.RpcService;
import com.example.rpc.server.function.interfaces.RemoteCalculate;

@RpcService(interfaceClass = RemoteCalculate.class)
public class RemoteCalculateImpl implements RemoteCalculate {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}