package com.example.rpc.client.annotation;

import com.example.rpc.client.client.RpcClientFactory;
import com.example.rpc.client.exception.RpcClientException;
import com.example.rpc.core.ClassScanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class RpcReferenceInjector {
    public static void inject(Object target) {
        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                injectField(field, target);
            }
        }
    }

    public static void autoInjectStaticFields(String... packageNames) {
        for (String packageName : packageNames) {
            try {
                List<Class<?>> classes = ClassScanner.scanClasses(packageName);
                for (Class<?> clazz : classes) {
                    injectStaticFields(clazz);
                }
            } catch (Exception e) {
                throw new RpcClientException("自动注入失败: " + e.getMessage());
            }
        }
    }

    private static void injectStaticFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RpcReference.class) && Modifier.isStatic(field.getModifiers())) {
                injectField(field, null);
            }
        }
    }

    private static void injectField(Field field, Object target) {
        try {
            Class<?> serviceInterface = field.getType();
            Object proxy = RpcClientFactory.create(serviceInterface);
            field.setAccessible(true);
            field.set(target, proxy);
        } catch (IllegalAccessException e) {
            throw new RpcClientException("注入失败: " + field.getName());
        }
    }
}