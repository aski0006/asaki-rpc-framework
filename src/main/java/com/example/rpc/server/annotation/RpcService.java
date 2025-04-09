
package com.example.rpc.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <p>RpcService 是一个用于标记 RPC 服务实现类的注解。</p>
 * <p>该注解用于标识一个类作为 RPC 服务的实现类，并允许指定服务接口和版本号。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *     <li><strong>标识服务实现类</strong>: 通过该注解标记的类将被识别为 RPC 服务的实现类。</li>
 *     <li><strong>指定服务接口</strong>: 可以通过 `interfaceClass` 属性指定服务接口类。</li>
 *     <li><strong>版本控制</strong>: 可以通过 `version` 属性指定服务的版本号，用于支持多版本服务。</li>
 * </ul>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    Class<?> interfaceClass() default void.class;
    String version() default "";
}