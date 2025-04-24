
package com.example.rpc.server.config;

import com.example.rpc.registry.config.RegistryConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        try {
            // 优先级 1: 从JAR所在目录的config文件夹加载
            String jarPath = ServerConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            String jarDir = jarFile.getParent(); // 获取JAR所在目录
            File externalConfig = new File(jarDir + File.separator + "config" + File.separator + "server.properties");
            System.out.println("尝试加载外部配置文件: " + externalConfig.getAbsolutePath());

            if (externalConfig.exists()) {
                try (InputStream input = new FileInputStream(externalConfig)) {
                    props.load(input);
                    System.out.println("成功加载外部配置文件");
                }
            } else {
                // 优先级 2: 从JAR内部加载
                System.out.println("外部配置文件不存在，尝试从JAR内部加载...");
                try (InputStream input = ServerConfig.class.getResourceAsStream("/server.properties")) {
                    if (input != null) {
                        props.load(input);
                        System.out.println("成功加载JAR内部配置文件");
                    } else {
                        throw new IOException("JAR内部未找到server.properties");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("配置文件加载失败，使用默认配置: " + e.getMessage());
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