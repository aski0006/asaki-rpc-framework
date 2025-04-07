package com.example.rpc.server.config;

import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    // 定义一个Properties对象，用于存储配置信息
    private static final Properties props = new Properties();

    // 静态代码块，用于加载配置文件
    static {
        try (InputStream input = ServerConfig.class.getResourceAsStream("/server.properties")) {
            // 从配置文件中加载配置信息
            props.load(input);
        } catch (Exception e) {
            // 如果加载配置文件失败，则使用默认配置
            props.setProperty("export.packages.path", "com.example.rpc.server.function.impl");

            props.setProperty("server.host", "localhost");
            props.setProperty("poll.interval", "5000");
            props.setProperty("heartbeat.interval", "30000");
            props.setProperty("server.port", "8080");
            props.setProperty("retry.interval", "30000");
            props.setProperty("registry.address", "localhost:2181");
        }
    }

    // 获取服务器端口号
    public static int getPort() {
        return Integer.parseInt(props.getProperty("server.port"));
    }

    // 获取注册中心地址
    public static String getRegistryAddress() {
        return props.getProperty("registry.address");
    }

    // 获取导出包路径
    public static String getExportPackagesPath() {
        return props.getProperty("export.packages.path");
    }

    // 获取重试间隔
    public static long getRetryInterval() {
        return Long.parseLong(props.getProperty("retry.interval"));
    }

    // 获取心跳间隔
    public static long getHeartbeatInterval() {
        return Long.parseLong(props.getProperty("heartbeat.interval"));
    }

    // 获取轮询间隔
    public static long getPollInterval() {
        return Long.parseLong(props.getProperty("poll.interval"));
    }

    public static String getServerHost() {
        return props.getProperty("server.host");
    }
}