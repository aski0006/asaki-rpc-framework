package com.example.rpc.client.loadbalance.impl;

import com.example.rpc.client.loadbalance.LoadBalancer;
import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>RoundRobinLoadBalancer 类实现了负载均衡器接口 {@link LoadBalancer}，使用轮询（Round Robin）算法来选择服务实例。</p>
 * <p>该类通过维护一个原子整数索引来确保在多线程环境下能够安全地进行轮询操作。</p>
 * <p>核心功能详细说明：</p>
 * <p>1. 初始化索引：使用 {@link AtomicInteger} 来维护一个原子整数索引，确保线程安全。</p>
 * <p>2. 选择服务实例：在 {@link #select(List)} 方法中，通过获取并递增索引来选择下一个服务实例，利用取模运算保证索引在服务实例列表范围内循环。</p>
 * <p>3. 异常处理：如果传入的服务实例列表为空，则抛出 {@link IllegalStateException} 异常。</p>
 *
 * <p>使用示例：</p>
 * <p>以下代码展示了如何创建 RoundRobinLoadBalancer 实例并选择服务实例。</p>
 * <pre>
 * RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer();
 * List<ServiceInstance> instances = ...; // 假设已经获取了服务实例列表
 * ServiceInstance selectedInstance = loadBalancer.select(instances);
 * </pre>
 *
 * <p>构造函数说明：</p>
 * <p>该类的构造函数不接受任何参数，可直接使用无参构造函数创建实例。</p>
 *
 * <p>使用限制与潜在副作用：</p>
 * <p>1. 该类必须在多线程环境中使用时，能够保证线程安全。</p>
 * <p>2. 传入的服务实例列表必须非空，否则会抛出异常。</p>
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger index = new AtomicInteger(0);

    /**
     * 从服务实例列表中选择一个服务实例，使用轮询算法。
     *
     * @param instances 服务实例列表
     * @return 选择的服务实例
     * @throws IllegalStateException 如果服务实例列表为空
     */
    @Override
    public ServiceInstance select(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            throw new IllegalStateException("No available service instances");
        }
        
        int size = instances.size();
        int nextIndex = index.getAndIncrement() % size;
        return instances.get(nextIndex);
    }
}