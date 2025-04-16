package com.example.rpc.launcher;

import com.example.rpc.registry.RpcRegistryStarter;
import com.example.rpc.server.RpcServerStarter;

import java.util.Arrays;

public class MainLauncher {
    public static void main(String[] args) {
        System.out.println("Hello World");
        try {
            if (args.length == 0) {
                System.err.println("Usage: <server|registry>");
                System.err.flush(); // 强制刷新错误流
                return;
            }
            System.out.println("[DEBUG] 启动命令: " + args[0]);
            System.out.flush(); // 强制刷新标准输出流

            switch (args[0]) {
                case "server":
                    RpcServerStarter.main(Arrays.copyOfRange(args, 1, args.length));
                    break;
                case "registry":
                    RpcRegistryStarter.main(Arrays.copyOfRange(args, 1, args.length));
                    break;
                default:
                    System.err.println("无效命令: " + args[0]);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}