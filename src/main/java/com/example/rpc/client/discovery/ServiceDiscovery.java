package com.example.rpc.client.discovery;

import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;

public interface ServiceDiscovery {
    List<ServiceInstance> discover(String serviceName);
}