package com.example.rpc.client.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    String version() default "";
}