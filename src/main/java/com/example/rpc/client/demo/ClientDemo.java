package com.example.rpc.client.demo;

import com.example.rpc.client.client.RpcClientFactory;
import com.example.rpc.client.exception.RpcClientException;
import com.example.rpc.server.function.interfaces.UserService;

public class ClientDemo {
    public static void main(String[] args) {
        // 创建服务代理（需捕获可能出现的异常）
        try {
            UserService userService = RpcClientFactory.create(UserService.class);

            // 同步调用示例
            String name = userService.getUserInfo(123L);
            System.out.println("User name: " + name);

        } catch (RpcClientException e) {
            System.err.println("RPC调用失败: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}