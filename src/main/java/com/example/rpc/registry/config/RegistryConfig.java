package com.example.rpc.registry.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
            // 动态获取JAR所在目录路径
            String jarPath = RegistryConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8); // 处理特殊字符和空格
            File jarFile = new File(jarPath);
            String jarDir = jarFile.getParent();

            // 优先级1: 从JAR同级config目录加载
            File externalConfig = new File(jarDir + File.separator + "config" + File.separator + "registry.properties");
            System.out.println("尝试加载外部配置: " + externalConfig.getAbsolutePath());

            if (externalConfig.exists()) {
                try (InputStream input = new FileInputStream(externalConfig)) {
                    props.load(input);
                    System.out.println("成功加载外部配置文件");
                }
            } else {
                // 优先级2: 从JAR内部加载
                System.out.println("外部配置不存在，尝试JAR内部...");
                try (InputStream input = RegistryConfig.class.getResourceAsStream("/registry.properties")) {
                    if (input != null) {
                        props.load(input);
                        System.out.println("成功加载JAR内部配置");
                    } else {
                        // 优先级3: 使用默认配置
                        System.out.println("JAR内部未找到配置，使用默认值");
                        props.setProperty("registry.port", "2181");
                        props.setProperty("heartbeat.interval", "3000");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("配置加载异常: " + e.getMessage());
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