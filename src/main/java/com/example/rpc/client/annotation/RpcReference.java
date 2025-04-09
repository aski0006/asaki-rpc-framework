package com.example.rpc.client.annotation;

import java.lang.annotation.*;

/**
 *
 *  @RpcReference 注解
 *  用于标识一个字段是一个远程服务引用，用于在客户端代码中注入远程服务。
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    String version() default "";
}