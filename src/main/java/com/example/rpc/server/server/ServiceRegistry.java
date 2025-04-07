package com.example.rpc.server.server;

import com.example.rpc.registry.config.RegistryConfig;
import com.example.rpc.registry.model.ServiceInstance;
import com.example.rpc.server.annotation.RpcService;
import com.example.rpc.server.config.ServerConfig;
import com.example.rpc.server.core.ClassScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceRegistry {
    private static final Map<String, Object> serviceMap = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Timer retryTimer = new Timer(true); // 使用守护线程
    private static final AtomicBoolean isRegistering = new AtomicBoolean(false);

    public static void registerServices(String packageScan) throws Exception {
        // 扫描指定包下带有@RpcService的类
        ClassScanner.scan(packageScan, RpcService.class).forEach(clazz -> {
            RpcService annotation = clazz.getAnnotation(RpcService.class);
            String serviceName = annotation.interfaceClass() != void.class ?
                    annotation.interfaceClass().getName() :
                    clazz.getInterfaces()[0].getName();

            System.out.println("Scanning class: " + clazz.getName());
            System.out.println("Service name: " + serviceName);

            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                serviceMap.put(serviceName, instance);
                System.out.println("Service instance created and added to map: " + serviceName);
                registerToRegistry(serviceName);
                System.out.println("Service registered to registry: " + serviceName);
            } catch (Exception e) {
                System.out.println("Service init failed: " + serviceName);
                System.out.println(e.getMessage());
                // 启动重试机制
                startRetryMechanism(serviceName);
            }
        });
    }

    private static void registerToRegistry(String serviceName) throws Exception {
        if (isRegistering.compareAndSet(false, true)) {
            try {
                ServiceInstance instance = new ServiceInstance();
                instance.setServiceName(serviceName);
                instance.setHost("localhost"); // 或实际服务IP
                instance.setPort(ServerConfig.getPort()); // 使用RPC服务器的端口（如8080）

                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpPost post = new HttpPost("http://" + ServerConfig.getRegistryAddress() + "/register");
                    post.setEntity(new StringEntity(mapper.writeValueAsString(instance)));
                    System.out.println(instance);
                    post.setHeader("Content-Type", "application/json");
                    client.execute(post);
                }
            } finally {
                isRegistering.set(false);
            }
        }
    }

    private static void startRetryMechanism(String serviceName) {
        retryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    registerToRegistry(serviceName);
                    System.out.println("Service registered to registry after retry: " + serviceName);
                    // 注册成功后取消定时任务
                    this.cancel();
                } catch (Exception e) {
                    System.out.println("Retry failed for service: " + serviceName);
                    System.out.println(e.getMessage());
                }
            }
        }, 0, ServerConfig.getRetryInterval()); // 重试间隔从配置中获取
    }

    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
}