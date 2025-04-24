package com.example.rpc.server.server;

import com.example.rpc.registry.model.ServiceInstance;
import com.example.rpc.server.annotation.RpcService;
import com.example.rpc.server.config.ServerConfig;
import com.example.rpc.core.ClassScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>服务注册类</p>
 * <p>该类负责服务的注册、心跳维护、重试机制以及资源清理。</p>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class ServiceRegistry {
    // 服务映射表，用于存储已注册的服务
    private static final Map<String, Object> serviceMap = new HashMap<>();
    // 已处理过的类集合，用于避免重复处理
    private static final Set<Class<?>> processedClasses = ConcurrentHashMap.newKeySet();
    // JSON序列化工具
    private static final ObjectMapper mapper = new ObjectMapper();
    // 重试定时器
    private static final Timer retryTimer = new Timer(true);
    // 是否正在注册服务的原子布尔值
    private static final AtomicBoolean isRegistering = new AtomicBoolean(false);
    // 轮询定时器
    private static Timer pollTimer;

    // 心跳定时器映射表，用于存储每个实例的心跳定时器
    private static final Map<String, Timer> heartbeatTimers = new ConcurrentHashMap<>();
    // 已注册实例映射表，用于存储已注册的实例
    private static final Map<String, ServiceInstance> registeredInstances = new ConcurrentHashMap<>();

    // 启动轮询
    public static void startPolling() {
        // 停止轮询
        stopPolling();
        // 创建定时器
        pollTimer = new Timer(true);
        // 获取轮询间隔
        long interval = ServerConfig.getPollInterval();
        // 定时执行任务
        pollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 获取需要扫描的包路径
                    String packageScan = ServerConfig.getExportPackagesPath();
                    // 注册服务
                    registerServices(packageScan);
                    // 打印轮询信息
                    System.out.println("[" + new Date() + "] Polling for new services...");
                } catch (Exception e) {
                    // 打印错误信息
                    System.out.println("Error while polling for new services: " + e.getMessage());
                    // 抛出运行时异常
                    throw new RuntimeException(e);
                }
            }
        }, 0, interval);
    }

    // 停止轮询
    public static void stopPolling() {
        if (pollTimer != null) {
            pollTimer.cancel();
            pollTimer = null;
        }
    }

    // 注册服务
    public static void registerServices(String packageScan) throws Exception {
        // 扫描指定包下的所有带有RpcService注解的类
        List<Class<?>> classes = ClassScanner.scan(packageScan, RpcService.class);
        for (Class<?> clazz : classes) {
            // 同步处理已处理的类
            synchronized (processedClasses) {
                // 如果已处理过该类，则跳过
                if (processedClasses.contains(clazz)) continue;
                processedClasses.add(clazz);
            }

            // 获取类的RpcService注解
            RpcService annotation = clazz.getAnnotation(RpcService.class);
            // 解析服务名
            String serviceName = resolveServiceName(clazz, annotation);

            System.out.println("Processing class: " + clazz.getName());
            System.out.println("Service name: " + serviceName);

            try {
                // 创建类的实例
                Object instance = clazz.getDeclaredConstructor().newInstance();
                // 将实例放入服务映射中
                serviceMap.put(serviceName, instance);
                // 注册服务到注册中心
                registerToRegistry(serviceName);
                System.out.println("Service registered successfully: " + serviceName);
            } catch (Exception e) {
                System.err.println("Service init failed: " + serviceName);
                // 启动重试机制
                startRetryMechanism(serviceName);
                System.out.println("Retry mechanism started for service: " + serviceName);
            }
        }
    }

    private static String resolveServiceName(Class<?> clazz, RpcService annotation) {
    // 解析服务名
        return annotation.interfaceClass() != void.class ?
                annotation.interfaceClass().getName() :
                clazz.getInterfaces()[0].getName();
    }

    private static void registerToRegistry(String serviceName) throws Exception {
    // 注册到注册中心
        if (!isRegistering.compareAndSet(false, true)) return;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ServiceInstance instance = new ServiceInstance();
            instance.setServiceName(serviceName);
            instance.setHost(ServerConfig.getServerHost());
            instance.setPort(ServerConfig.getPort());
            // 生成唯一实例ID
            UUID instanceId = UUID.randomUUID();
            instance.setInstanceId(instanceId);

            // 发送注册请求
            HttpPost post = new HttpPost("http://" + ServerConfig.getRegistryAddress() + "/register");
            post.setEntity(new StringEntity(mapper.writeValueAsString(instance)));
            post.setHeader("Content-Type", "application/json");
            client.execute(post);

            // 注册成功后启动心跳定时任务
            startHeartbeatTask(instance);
            registeredInstances.put(instanceId.toString(), instance);
        } finally {
            isRegistering.set(false);
        }
    }

    // 新增：启动心跳定时任务
    private static void startHeartbeatTask(ServiceInstance instance) {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat(instance.getServiceName(), instance.getInstanceId().toString());
            }
        };
        // 按照配置的心跳间隔定期发送
        timer.scheduleAtFixedRate(task, 0, ServerConfig.getHeartbeatInterval());
        heartbeatTimers.put(instance.getInstanceId().toString(), timer);
    }

    // 新增：发送心跳请求
    private static void sendHeartbeat(String serviceName, String instanceId) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://" + ServerConfig.getRegistryAddress() + "/heartbeat");
            Map<String, String> payload = new HashMap<>();
            payload.put("serviceName", serviceName);
            payload.put("instanceId", instanceId);
            post.setEntity(new StringEntity(mapper.writeValueAsString(payload)));
            post.setHeader("Content-Type", "application/json");
            client.execute(post);
            System.out.println("Heartbeat sent for instance: " + instanceId);
        } catch (Exception e) {
            System.err.println("Failed to send heartbeat for instance: " + instanceId);
        }
    }

    // 新增：关闭时清理资源
    public static void shutdown() {
        // 取消所有心跳任务
        heartbeatTimers.forEach((instanceId, timer) -> {
            timer.cancel();
        });
        heartbeatTimers.clear();

        // 注销所有服务实例
        registeredInstances.forEach((instanceId, instance) -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost("http://" + ServerConfig.getRegistryAddress() + "/unregister");
                post.setEntity(new StringEntity(mapper.writeValueAsString(instance)));
                post.setHeader("Content-Type", "application/json");
                client.execute(post);
                System.out.println("Service unregistered: " + instance.getServiceName());
            } catch (Exception e) {
                System.err.println("Failed to unregister instance: " + instanceId);
            }
        });
        registeredInstances.clear();
    }

    // 启动重试机制
    private static void startRetryMechanism(String serviceName) {
        retryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    registerToRegistry(serviceName);
                    this.cancel();
                } catch (Exception e) {
                    System.err.println("Retry failed: " + serviceName);
                }
            }
        }, 0, ServerConfig.getRetryInterval());
    }

    // 获取服务
    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
}