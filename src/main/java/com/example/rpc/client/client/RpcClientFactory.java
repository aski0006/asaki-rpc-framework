// RpcClientFactory.java
package com.example.rpc.client.client;

import com.example.rpc.client.proxy.RpcClientProxy;
import java.lang.reflect.Proxy;

/**
 * <p>RpcClientFactory 类用于创建 RPC 客户端代理对象。</p>
 * <p>它通过 Java 动态代理机制，为指定的接口创建代理对象。</p>
 *
 * <p>核心功能：</p>
 * <p>- 创建指定接口的代理对象</p>
 *
 * <p>使用示例：</p>
 * <p>RpcClientFactory.create(MyService.class);</p>
 *
 * <p>构造函数参数：</p>
 * <p>- interfaceClass: 需要创建代理对象的接口类</p>
 *
 * <p>注意事项：</p>
 * <p>- 该类使用 Java 动态代理机制，因此要求接口类必须被正确加载</p>
 * <p>- 使用该类创建的代理对象，其行为将根据 RpcClientProxy 类的实现而变化</p>
 */
public class RpcClientFactory {
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcClientProxy(interfaceClass)
        );
    }
}