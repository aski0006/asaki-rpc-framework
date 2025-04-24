package com.example.rpc.function.impl;

import com.example.rpc.function.interfaces.UserService;
import com.example.rpc.server.annotation.RpcService;

@RpcService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public String getUserInfo(Long userId) {
        return "User_" + userId;
    }
}