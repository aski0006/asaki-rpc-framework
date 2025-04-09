
package com.example.rpc.server.config;

import java.io.InputStream;
import java.util.Properties;
/**
 * <p>ServerConfig 是 RPC 服务器的配置类。</p>
 * <p>该类负责加载和管理服务器的配置信息，包括端口号、注册中心地址、导出包路径等。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>加载配置文件</strong>: 从 `server.properties` 文件中加载配置信息，如果加载失败则使用默认配置。</li>
 *     <li><strong>提供配置获取方法</strong>: 提供了一系列静态方法，用于获取服务器端口号、注册中心地址、导出包路径等配置信息。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
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