package com.example.rpc.client.config;

import com.example.rpc.registry.config.RegistryConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>ClientConfig 类的主要作用是读取客户端配置文件中的属性，并提供获取这些属性的方法。</p>
 * <p>配置文件路径为 /client.properties，如果文件读取失败，则会使用默认配置。</p>
 * <p>默认配置包括：</p>
 * <ul>
 *     <li>registry.address: 注册中心地址，默认为 localhost:2181</li>
 *     <li>load.balance.strategy: 负载均衡策略，默认为 roundRobin</li>
 * </ul>
 * @Author 郑钦 (Asaki0019)
 * @Date 2025/4/8
 */
public class ClientConfig {
    private static final Properties props = new Properties();

    static {
        try {
            // 优先级 1: 从外部目录加载（例如 JAR 同级目录下的 config 文件夹）
            File externalConfig = new File("./config/client.properties");
            if (externalConfig.exists()) {
                try (InputStream input = new FileInputStream(externalConfig)) {
                    props.load(input);
                }
            } else {
                // 优先级 2: 从 JAR 内部加载
                try (InputStream input = RegistryConfig.class.getResourceAsStream("/client.properties")) {
                    props.load(input);
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            props.setProperty("registry.address", "localhost:2181");
            props.setProperty("request.timeout", "5000");
            props.setProperty("load.balance.strategy", "roundRobin");
        }
    }

    /**
     * 获取注册中心地址。
     * @return 注册中心地址
     */
    public static String getRegistryAddress() {
        return props.getProperty("registry.address");
    }

    /**
     * 获取负载均衡策略。
     * @return 负载均衡策略
     */
    public static String getLoadBalanceStrategy() {
        return props.getProperty("load.balance.strategy");
    }

    /**
     * 获取请求超时时间。
     * @return 请求超时时间（毫秒）
     */
    public static int getRequestTimeout() {
        return Integer.parseInt(props.getProperty("request.timeout"));
    }
}