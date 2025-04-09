package com.example.rpc.core.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * <p>RpcConfig 是一个用于加载 RPC 配置的工具类。</p>
 * <p>它从类路径下的 rpc.properties 文件中读取配置信息。</p>
 * <p>如果文件不存在，则使用默认配置：</p>
 * <ul>
 *   <li>rpc.server.host=localhost</li>
 *   <li>rpc.server.port=8080</li>
 * </ul>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li><strong>静态代码块</strong>: 尝试加载 rpc.properties 文件，若失败则设置默认配置。</li>
 *   <li><strong>getProperty 方法</strong>: 根据键获取配置属性值。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class RpcConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = RpcConfig.class.getResourceAsStream("/rpc.properties")) {
            props.load(input);
        } catch (Exception e) {
            System.out.println("Cant found rpc.properties, user default config, " +
                    "rpc.server.host=localhost, rpc.server.port=8080");
            props.setProperty("rpc.server.host", "localhost");
            props.setProperty("rpc.server.port", "8080");
        }
    }

    /**
     * 根据指定的键获取配置属性值。
     *
     * @param key 配置属性的键。
     * @return 对应键的配置值，如果键不存在则返回 null。
     */
    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}