package com.example.rpc.registry.service;
import com.example.rpc.registry.config.RegistryConfig;
import com.example.rpc.registry.model.ServiceInstance;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>MemoryRegistryService 是一个基于内存的服务注册表实现类。</p>
 * <p>它使用 ConcurrentHashMap 来存储服务实例，并通过定时任务检查服务实例的心跳状态。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *     <li><strong>register</strong>: 注册新的服务实例。</li>
 *     <li><strong>unregister</strong>: 注销已有的服务实例。</li>
 *     <li><strong>discoveryServiceInstance</strong>: 发现指定服务名称的所有服务实例。</li>
 *     <li><strong>heartbeat</strong>: 更新服务实例的心跳时间戳。</li>
 *     <li><strong>checkHeartbeats</strong>: 定时检查并移除过期的服务实例。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class MemoryRegistryService implements RegistryService {
    private static Map<String, Map<String, ServiceInstance>> serviceRegistry = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public MemoryRegistryService() {
        scheduler.scheduleAtFixedRate(this::checkHeartbeats,
                0, RegistryConfig.getHeartbeatInterval(), TimeUnit.MILLISECONDS);
    }

    /**
     * 注册一个新的服务实例。
     *
     * @param instance 要注册的服务实例。
     */
    @Override
    public synchronized void register(ServiceInstance instance) {
        try{
            String serviceName = instance.getServiceName();
            serviceRegistry.computeIfAbsent(serviceName, k -> new ConcurrentHashMap<>())
                    .put(instance.getInstanceId().toString(), instance);
            System.out.println("( + ) Service registered: " + serviceName);
        }catch (Exception e){
            System.out.println("Service init failed: " + instance.getServiceName());
        }
    }

    /**
     * 注销已有的服务实例。
     *
     * @param instance 要注销的服务实例。
     */
    @Override
    public synchronized void unregister(ServiceInstance instance) {
        Map<String, ServiceInstance> instances = serviceRegistry.get(instance.getServiceName());
        if (instances != null) {
            instances.remove(instance.getInstanceId().toString());
        }
    }

    /**
     * 发现指定服务名称的所有服务实例。
     *
     * @param serviceName 服务名称。
     * @return 服务实例列表。
     */
    @Override
    public List<ServiceInstance> discoveryServiceInstance(String serviceName) {
        Map<String, ServiceInstance> instances = serviceRegistry.get(serviceName);
        return instances != null ? new ArrayList<>(instances.values()) : Collections.emptyList();
    }

    /**
     * 更新服务实例的心跳时间戳。
     *
     * @param serviceName 服务名称。
     * @param instanceId 服务实例的唯一标识符。
     */
    @Override
    public void heartbeat(String serviceName, String instanceId) {
        if (serviceRegistry.containsKey(serviceName)) {
            Map<String, ServiceInstance> instances = serviceRegistry.get(serviceName);
            if (instances != null && instances.containsKey(instanceId)) {
                instances.get(instanceId).setLastUpdateTimestamp(System.currentTimeMillis());
            }
        }
    }

    /**
     * 定时检查并移除过期的服务实例。
     * 如果服务实例的心跳时间戳超过 HEARTBEAT_INTERVAL 的两倍，则认为该实例已过期。
     */
    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        serviceRegistry.forEach((serviceName, instances) -> {
            Iterator<Map.Entry<String, ServiceInstance>> iterator = instances.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ServiceInstance> entry = iterator.next();
                ServiceInstance instance = entry.getValue();
                if ((now - instance.getLastUpdateTimestamp()) > RegistryConfig.getHeartbeatInterval() * 2) {
                    // 打印过期的服务实例信息
                    System.out.println("( - ) Expired Service Instance: Service=" + serviceName +
                                       ", InstanceId=" + instance.getInstanceId() + 
                                       ", LastUpdate=" + instance.getLastUpdateTimestamp());
                    iterator.remove(); // 移除过期实例
                }
            }
        });
        System.out.println("------------ Current Service Instance -----------");
        // 打印当前剩余的服务实例信息
        serviceRegistry.forEach((serviceName, instances) -> {
            instances.forEach((instanceId, instance) -> {
                System.out.println("Current Service Instance: Service=" + serviceName + 
                                   ", InstanceId=" + instance.getInstanceId() + 
                                   ", LastUpdate=" + instance.getLastUpdateTimestamp());
            });
        });
        System.out.println(" ");
    }
}