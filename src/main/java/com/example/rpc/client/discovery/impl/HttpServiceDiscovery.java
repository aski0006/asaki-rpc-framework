package com.example.rpc.client.discovery.impl;

import com.example.rpc.client.config.ClientConfig;
import com.example.rpc.client.discovery.ServiceDiscovery;
import com.example.rpc.registry.model.ServiceInstance;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.List;

/**
 * <p>HttpServiceDiscovery 类的主要作用是通过 HTTP 请求从注册中心发现服务实例。</p>
 * <p>该类实现了 ServiceDiscovery 接口，并使用 Apache HttpClient 发送 HTTP GET 请求到注册中心的 discovery 端点，
 * 以获取指定服务名称的服务实例列表。</p>
 */
public class HttpServiceDiscovery implements ServiceDiscovery {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 发现指定服务名称的服务实例列表。
     * @param serviceName 要发现的服务名称
     * @return 服务实例列表
     * @throws RuntimeException 如果服务发现过程中发生异常
     */
    @Override
    public List<ServiceInstance> discover(String serviceName) {
        String registryUrl = "http://" + ClientConfig.getRegistryAddress() + "/discovery?service=" + serviceName;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(registryUrl);
            return client.execute(request, response ->
                    mapper.readValue(response.getEntity().getContent(),
                            new TypeReference<>() {
                            }));
        } catch (Exception e) {
            throw new RuntimeException("Service discovery failed", e);
        }
    }
}