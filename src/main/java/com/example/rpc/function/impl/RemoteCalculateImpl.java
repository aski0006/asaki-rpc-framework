package com.example.rpc.function.impl;

import com.example.rpc.server.annotation.RpcService;
import com.example.rpc.function.interfaces.RemoteCalculate;

@RpcService(interfaceClass = RemoteCalculate.class)
public class RemoteCalculateImpl implements RemoteCalculate {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}