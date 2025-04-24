package com.example.rpc.client.annotation;

import java.lang.annotation.*;


/**
 * RpcReference 是一个自定义注解，用于标记需要远程过程调用（RPC）引用的字段。
 *
 * 该注解用于在应用程序中标识需要通过RPC框架进行远程调用的服务接口字段。
 * 核心功能包括指定服务的版本号，以便在服务发现和调用时能够精确匹配到所需的服务实例。
 *
 * 使用示例：
 * <pre>
 * {@code
 * @RpcReference(version = "1.0.0")
 * private MyService myService;
 * }
 * </pre>
 *
 * 构造函数参数：
 * - version：指定RPC服务的版本号，默认为空字符串。用于区分不同版本的服务接口。
 *
 * 特殊使用限制或潜在的副作用：
 * - 该注解只能用于字段上，不能用于其他元素（如方法、类等）。
 * - 如果未指定版本号，可能会匹配到错误的服务实例，导致调用失败或行为异常。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
}