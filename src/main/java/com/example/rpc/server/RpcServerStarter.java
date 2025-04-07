package com.example.rpc.server;

import com.example.rpc.server.config.ServerConfig;
import com.example.rpc.server.server.NettyServer;
import com.example.rpc.server.server.ServiceRegistry;

public class RpcServerStarter {
    public static void start() throws Exception {
        ServiceRegistry.registerServices(ServerConfig.getExportPackagesPath());
        new NettyServer(ServerConfig.getPort()).start();
    }

    public static void main(String[] args) throws Exception {
        start();
    }
}