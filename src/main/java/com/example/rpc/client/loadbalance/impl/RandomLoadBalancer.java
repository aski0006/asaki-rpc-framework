package com.example.rpc.client.loadbalance.impl;

import com.example.rpc.client.loadbalance.LoadBalancer;
import com.example.rpc.registry.model.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>RandomLoadBalancer 是一个负载均衡器实现，它通过随机选择服务实例来分配请求。</p>
 * <p>该类实现了 LoadBalancer 接口，通过 select 方法从给定的服务实例列表中随机选择一个实例。</p>
 * <br>
 * <p>核心功能：</p>
 * <p>- 从服务实例列表中随机选择一个实例。</p>
 * <br>
 * <ui>使用示例：</ui>
 * <li>RandomLoadBalancer loadBalancer = new RandomLoadBalancer();</li>
 * <li>List<ServiceInstance> instances = ... // 获取服务实例列表</li>
 * <li>ServiceInstance selectedInstance = loadBalancer.select(instances);</li>
 * <br>
 * <p>构造函数参数：</p>
 * <p>- 无</p>
 * <p>使用限制：</p>
 * <p>- 如果服务实例列表为空，select 方法将抛出 IllegalStateException。</p>
 * <p>潜在副作用：</p>
 * <p>- 由于是随机选择，可能会导致某些服务实例被频繁选择，而其他实例较少被选择。</p>
 * @Author 郑钦 (Asaki0019)
 * @Date 2025/4/8
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public ServiceInstance select(List<ServiceInstance> instances) {
        try {
            System.out.println("Entering select method with instances: " + instances);
            if (instances.isEmpty()) {
                System.out.println("No available service instances");
            }
            int index = ThreadLocalRandom.current().nextInt(instances.size());
            System.out.println("Selected index: " + index);
            return instances.get(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid index selected from service instances");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred while selecting a service instance");
        }
        return null;
    }
}