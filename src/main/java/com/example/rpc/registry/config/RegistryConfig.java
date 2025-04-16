package com.example.rpc.registry.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>RegistryConfig 是一个用于加载注册中心配置的工具类。</p>
 * <p>它从类路径下的 registry.properties 文件中读取配置信息。</p>
 * <p>如果文件不存在，则使用默认配置：</p>
 * <ul>
 *   <li>registry.port=2181</li>
 *   <li>heartbeat.interval=30000</li>
 * </ul>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li><strong>静态代码块</strong>: 尝试加载 registry.properties 文件，若失败则设置默认配置。</li>
 *   <li><strong>getPort 方法</strong>: 获取注册中心的端口号。</li>
 *   <li><strong>getHeartbeatInterval 方法</strong>: 获取心跳间隔时间。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RegistryConfig {
    private static final Properties props = new Properties();

    static {
        try {
            // 优先级 1: 从外部目录加载（例如 JAR 同级目录下的 config 文件夹）
            File externalConfig = new File("./config/registry.properties");
            if (externalConfig.exists()) {
                try (InputStream input = new FileInputStream(externalConfig)) {
                    props.load(input);
                }
            } else {
                // 优先级 2: 从 JAR 内部加载
                try (InputStream input = RegistryConfig.class.getResourceAsStream("/registry.properties")) {
                    props.load(input);
                }
            }
        } catch (Exception e) {
            // 优先级 3: 使用默认配置
            props.setProperty("registry.port", "2181");
            props.setProperty("heartbeat.interval", "3000");
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