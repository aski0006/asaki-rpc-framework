package com.example.rpc.registry.service;

import com.example.rpc.registry.model.ServiceInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryRegistryServiceTest {

    private MemoryRegistryService registryService;
    private Map<String, Map<String, ServiceInstance>> serviceRegistry;

    @BeforeEach
    public void setUp() throws Exception {
        registryService = new MemoryRegistryService();
        // 通过反射获取真实注册表引用
        Field registryField = MemoryRegistryService.class.getDeclaredField("serviceRegistry");
        registryField.setAccessible(true);
        serviceRegistry = (Map<String, Map<String, ServiceInstance>>) registryField.get(registryService);
    }

    @Test
    public void discoveryServiceInstance_ExistingServiceName_ReturnsServiceInstances() {
        // 准备
        String serviceName = "testService";
        ServiceInstance instance1 = new ServiceInstance(serviceName, "localhost", 8080, System.currentTimeMillis(), null);
        ServiceInstance instance2 = new ServiceInstance(serviceName, "localhost", 8081, System.currentTimeMillis(), null);
        
        registryService.register(instance1);
        registryService.register(instance2);

        // 执行
        List<ServiceInstance> instances = registryService.discoveryServiceInstance(serviceName);

        // 验证
        assertEquals(2, instances.size());
        assertTrue(instances.contains(instance1));
        assertTrue(instances.contains(instance2));
    }

    @Test
    public void discoveryServiceInstance_NonExistingServiceName_ReturnsEmptyList() {
        // 准备
        String serviceName = "nonExistingService";

        // 执行
        List<ServiceInstance> instances = registryService.discoveryServiceInstance(serviceName);

        // 验证
        assertTrue(instances.isEmpty());
    }

    @Test
    public void unregister_InstanceExists_RemovesInstance() {
        // 准备
        String serviceName = "testService";
        UUID instanceId = UUID.randomUUID();
        ServiceInstance instance = new ServiceInstance(serviceName, "localhost", 8080, System.currentTimeMillis(), new HashMap<>());
        instance.setInstanceId(instanceId);

        // 将实例添加到注册表中
        registryService.register(instance);

        // 验证实例存在
        assertNotNull(registryService.discoveryServiceInstance(serviceName));

        // 注销实例
        registryService.unregister(instance);

        // 验证实例已被移除
        assertNull(registryService.discoveryServiceInstance(serviceName).stream().filter(i -> i.getInstanceId().toString().equals(instanceId.toString())).findFirst().orElse(null));
    }

    @Test
    public void unregister_InstanceDoesNotExist_NoExceptionThrown() {
        // 准备
        String serviceName = "nonExistentService";
        UUID instanceId = UUID.randomUUID();
        ServiceInstance instance = new ServiceInstance(serviceName, "localhost", 8080, System.currentTimeMillis(), new HashMap<>());
        instance.setInstanceId( instanceId);

        // 尝试注销不存在的实例
        assertDoesNotThrow(() -> registryService.unregister(instance));
    }

    @Test
    public void unregister_EmptyRegistry_NoExceptionThrown() {
        // 准备
        String serviceName = "emptyRegistryService";
        UUID instanceId = UUID.randomUUID();
        ServiceInstance instance = new ServiceInstance(serviceName, "localhost", 8080, System.currentTimeMillis(), new HashMap<>());
        instance.setInstanceId( instanceId);

        // 尝试在空注册表中注销实例
        assertDoesNotThrow(() -> registryService.unregister(instance));
    }

    @Test
    public void heartbeat_ServiceNameDoesNotExist_NoUpdate() {
        String serviceName = "nonExistentService";
        String instanceId = UUID.randomUUID().toString();
        long initialTimestamp = System.currentTimeMillis();

        registryService.heartbeat(serviceName, instanceId);

        assertEquals(0, serviceRegistry.size());
    }

    @Test
    public void heartbeat_ServiceNameExistsInstanceIdDoesNotExist_NoUpdate() {
        String serviceName = "existingService";
        String instanceId = UUID.randomUUID().toString();
        long initialTimestamp = System.currentTimeMillis();

        Map<String, ServiceInstance> instances = new HashMap<>();
        ServiceInstance instance = new ServiceInstance();
        instance.setLastUpdateTimestamp(initialTimestamp);
        instances.put("anotherInstanceId", instance);
        serviceRegistry.put(serviceName, instances);

        registryService.heartbeat(serviceName, instanceId);

        assertEquals(initialTimestamp, instance.getLastUpdateTimestamp());
    }

    @Test
    public void heartbeat_ServiceNameAndInstanceIdExist_UpdatesTimestamp() {
        String serviceName = "existingService";
        long initialTimestamp = System.currentTimeMillis();

        // 创建实例并手动设置时间戳
        ServiceInstance instance = new ServiceInstance();
        String instanceId = instance.getInstanceId().toString();
        instance.setServiceName(serviceName); // 必须设置服务名称
        instance.setLastUpdateTimestamp(initialTimestamp);

        // 将实例添加到真实注册表
        serviceRegistry.computeIfAbsent(serviceName, k -> new ConcurrentHashMap<>())
                .put(instanceId, instance);

        // 执行心跳
        registryService.heartbeat(serviceName, instanceId);

        // 验证时间戳更新
        assertNotEquals(initialTimestamp, instance.getLastUpdateTimestamp());
    }

    @Test
    public void register_NewServiceName_ShouldAddToRegistry() throws NoSuchFieldException, IllegalAccessException {
        ServiceInstance instance = new ServiceInstance("testService", "localhost", 8080, System.currentTimeMillis(), null);
        registryService.register(instance);

        java.lang.reflect.Field serviceRegistryField = MemoryRegistryService.class.getDeclaredField("serviceRegistry");
        serviceRegistryField.setAccessible(true);
        Map<String, Map<String, ServiceInstance>> serviceRegistry = (Map<String, Map<String, ServiceInstance>>) serviceRegistryField.get(registryService);
        Map<String, ServiceInstance> serviceMap = serviceRegistry.get("testService");
        assertNotNull(serviceMap);
        assertEquals(1, serviceMap.size());
        assertEquals(instance, serviceMap.get(instance.getInstanceId().toString()));
    }

    @Test
    public void register_ExistingServiceName_ShouldAddNewInstance() throws NoSuchFieldException, IllegalAccessException {
        ServiceInstance instance1 = new ServiceInstance("testService", "localhost", 8080, System.currentTimeMillis(), null);
        ServiceInstance instance2 = new ServiceInstance("testService", "localhost", 8081, System.currentTimeMillis(), null);

        registryService.register(instance1);
        registryService.register(instance2);

        java.lang.reflect.Field serviceRegistryField = MemoryRegistryService.class.getDeclaredField("serviceRegistry");
        serviceRegistryField.setAccessible(true);
        Map<String, Map<String, ServiceInstance>> serviceRegistry = (Map<String, Map<String, ServiceInstance>>) serviceRegistryField.get(registryService);
        Map<String, ServiceInstance> serviceMap = serviceRegistry.get("testService");
        assertNotNull(serviceMap);
        assertEquals(2, serviceMap.size());
        assertEquals(instance1, serviceMap.get(instance1.getInstanceId().toString()));
        assertEquals(instance2, serviceMap.get(instance2.getInstanceId().toString()));
    }

    @Test
    public void register_NullServiceInstance_ShouldThrowException() {
        try {
            registryService.register(null);
        } catch (NullPointerException e) {
            // 验证抛出的异常类型
            assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    public void register_EmptyServiceName_ShouldAddToRegistry() throws NoSuchFieldException, IllegalAccessException {
        ServiceInstance instance = new ServiceInstance("", "localhost", 8080, System.currentTimeMillis(), null);
        registryService.register(instance);

        java.lang.reflect.Field serviceRegistryField = MemoryRegistryService.class.getDeclaredField("serviceRegistry");
        serviceRegistryField.setAccessible(true);
        Map<String, Map<String, ServiceInstance>> serviceRegistry = (Map<String, Map<String, ServiceInstance>>) serviceRegistryField.get(registryService);
        Map<String, ServiceInstance> serviceMap = serviceRegistry.get("");
        assertNotNull(serviceMap);
        assertEquals(1, serviceMap.size());
        assertEquals(instance, serviceMap.get(instance.getInstanceId().toString()));
    }
}
