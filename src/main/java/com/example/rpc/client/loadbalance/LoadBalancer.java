package com.example.rpc.client.loadbalance;

import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;

/**
 *  <p>负载均衡器接口</p>
 * @Author 郑钦 (Asaki0019)
 * @Date 2025/4/8
 */
public interface LoadBalancer {
    ServiceInstance select(List<ServiceInstance> instances);
}
