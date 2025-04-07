package com.example.rpc.client.loadbalance;

import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;

public interface LoadBalancer {
    ServiceInstance select(List<ServiceInstance> instances);
}
