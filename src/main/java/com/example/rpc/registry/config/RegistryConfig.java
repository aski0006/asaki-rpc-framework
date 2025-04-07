package com.example.rpc.registry.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * RegistryConfig 是一个用于加载注册中心配置的工具类。
 * 它从类路径下的 registry.properties 文件中读取配置信息。
 * 如果文件不存在，则使用默认配置：
 * <ul>
 *   <li>registry.port=2181</li>
 *   <li>heartbeat.interval=30000</li>
 * </ul>
 * 
 * 主要功能：
 * - 静态代码块：尝试加载 registry.properties 文件，若失败则设置默认配置。
 * - getPort 方法：获取注册中心的端口号。
 * - getHeartbeatInterval 方法：获取心跳间隔时间。
 */
public class RegistryConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = RegistryConfig.class.getResourceAsStream("/registry.properties")) {
            props.load(input);
        } catch (Exception e) {
            props.setProperty("registry.port", "2181");
            props.setProperty("heartbeat.interval", "30000");
        }
    }

    /**
     * 获取注册中心的端口号。
     *
     * @return 注册中心的端口号。
     */
    public static int getPort() {
        return Integer.parseInt(props.getProperty("registry.port"));
    }

    /**
     * 获取心跳间隔时间。
     *
     * @return 心跳间隔时间（毫秒）。
     */
    public static long getHeartbeatInterval() {
        return Long.parseLong(props.getProperty("heartbeat.interval"));
    }


}