package com.example.rpc.registry.service;

import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;
import java.util.UUID;

/**
 * <p>RegistryService 是注册中心服务的核心接口。</p>
 * <p>该接口定义了服务注册、注销、心跳检测以及服务发现等核心功能。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>服务注册</strong>: 允许服务实例向注册中心注册自己。</li>
 *     <li><strong>服务注销</strong>: 允许服务实例从注册中心注销自己。</li>
 *     <li><strong>心跳检测</strong>: 允许服务实例定期发送心跳以保持活跃状态。</li>
 *     <li><strong>服务发现</strong>: 允许客户端发现指定服务的所有可用实例。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public interface RegistryService {
    void register(ServiceInstance serviceInstance);
    void unregister(ServiceInstance serviceInstance);
    void heartbeat(String serviceName, String instanceId);
    List<ServiceInstance> discoveryServiceInstance(String serviceName);
}