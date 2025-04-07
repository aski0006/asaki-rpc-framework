package com.example.rpc.registry.service;

import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;
import java.util.UUID;

public interface RegistryService {
    void register(ServiceInstance serviceInstance);
    void unregister(ServiceInstance serviceInstance);
    void heartbeat(String serviceName, String instanceId);
    List<ServiceInstance> discoveryServiceInstance(String serviceName);
}
