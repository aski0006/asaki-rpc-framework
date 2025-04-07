package com.example.rpc.registry.service;
import com.example.rpc.registry.model.ServiceInstance;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MemoryRegistryService 是一个基于内存的服务注册表实现类。
 * 它使用 ConcurrentHashMap 来存储服务实例，并通过定时任务检查服务实例的心跳状态。
 * 
 * 主要功能：
 * <p>- register: 注册新的服务实例。</p>
 * <p>- unregister: 注销已有的服务实例。</p>
 * <p>- discoveryServiceInstance: 发现指定服务名称的所有服务实例。</p>
 * <p>- heartbeat: 更新服务实例的心跳时间戳。</p>
 * <p>- checkHeartbeats: 定时检查并移除过期的服务实例。</p>
 */
public class MemoryRegistryService implements RegistryService {
    private static final long HEARTBEAT_INTERVAL = 30_000;
    private static Map<String, Map<String, ServiceInstance>> serviceRegistry = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public MemoryRegistryService() {
        scheduler.scheduleAtFixedRate(this::checkHeartbeats,
                0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
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
            System.out.println("Service registered: " + serviceName);
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
        serviceRegistry.forEach((serviceName, instances) ->
                instances.values().removeIf(instance ->
                        (now - instance.getLastUpdateTimestamp()) > HEARTBEAT_INTERVAL * 2)
        );
        System.out.println("Service Instance Counts: " + serviceRegistry.size());
    }
}