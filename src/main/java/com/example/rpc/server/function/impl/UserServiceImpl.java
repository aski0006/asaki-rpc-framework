package com.example.rpc.server.function.impl;

import com.example.rpc.server.annotation.RpcService;
import com.example.rpc.server.function.interfaces.UserService;

@RpcService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public String getUserInfo(Long userId) {
        return "User_" + userId;
    }
}