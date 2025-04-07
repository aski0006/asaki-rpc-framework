package com.example.rpc.registry.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>ServiceInstance 类用于表示一个服务实例，包括服务名称、地址、端口、最后更新时间戳和元数据。</p>
 * <p>该类提供了获取和设置这些属性的方法。</p>
 *
 * <p>核心功能包括：</p>
 * <ul>
 *     <li>构造函数：用于创建一个新的 ServiceInstance 对象。</li>
 *     <li>获取和设置服务名称、地址、端口、最后更新时间戳和元数据的方法。</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * ServiceInstance instance = new ServiceInstance("exampleService", "192.168.1.1", 8080, System.currentTimeMillis(), new HashMap<>());
 * instance.setServiceName("newServiceName");
 * String serviceName = instance.getServiceName();
 * </pre>
 *
 * <p>构造函数参数：</p>
 * <ul>
 *     <li>serviceName: 服务名称，类型为 String。</li>
 *     <li>host: 服务地址，类型为 String。</li>
 *     <li>port: 服务端口，类型为 int。</li>
 *     <li>lastUpdateTimestamp: 最后更新时间戳，类型为 long。</li>
 *     <li>metaData: 元数据，类型为 Map&lt;String, String&gt;。</li>
 * </ul>
 *
 * <p>注意事项：</p>
 * <ul>
 *     <li>如果不提供元数据，构造函数会使用一个空的 HashMap。</li>
 *     <li>该类不提供任何特殊的使用限制或潜在的副作用。</li>
 * </ul>
 */
public class ServiceInstance {
    private UUID instanceId = UUID.randomUUID();
    private String serviceName;
    private String host;
    private int port;
    private long lastUpdateTimestamp;
    private Map<String, String> metaData = new HashMap<>();

    public ServiceInstance(){}

    public ServiceInstance(String serviceName, String host, int port, long lastUpdateTimestamp, Map<String, String> metaData) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        this.metaData = metaData;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(UUID instanceId) {
        this.instanceId = instanceId;
    }
}