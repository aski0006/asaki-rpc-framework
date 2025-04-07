package com.example.rpc.core.Serialize;

public interface RpcSerializer {
    <T> byte[] serialize(T object);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
