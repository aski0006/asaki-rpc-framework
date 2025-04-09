package com.example.rpc.core.Serialize;
import com.example.rpc.core.exception.SerializationException;
import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * <p>
 * JacksonSerializer 类是一个用于实现基于 Jackson 的序列化和反序列化功能的工具类。
 * 它提供了 Netty 处理器的实现，用于在网络传输中对对象进行序列化和反序列化操作。
 * </p>
 *
 * @author 郑钦 (Asaki0019)
 * @date 2025/4/8
 */
public class JacksonSerializer {
    /**
     * <p>
     * 静态的 ObjectMapper 实例，用于执行 JSON 序列化和反序列化操作。
     * 该实例在整个类中共享使用。
     * </p>
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * <p>
     * Encoder 类是一个 Netty 的编码器，用于将对象序列化为字节流。
     * 它继承自 MessageToByteEncoder，负责将传入的对象转换为字节数组并写入 ByteBuf 中。
     * </p>
     */
    public static class Encoder extends MessageToByteEncoder<Object> {
        /**
         * <p>
         * 该方法用于将传入的对象编码为字节流并写入 ByteBuf 中。
         * 使用 Jackson 的 ObjectMapper 将对象转换为字节数组，然后将字节数组写入 ByteBuf。
         * 如果序列化过程中出现异常，将触发异常捕获机制。
         * </p>
         *
         * @param ctx 通道处理上下文，用于触发异常捕获等操作
         * @param msg 要编码的对象
         * @param out 用于写入字节流的 ByteBuf
         * @throws Exception 如果序列化过程中出现异常
         */
        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            try {
                byte[] bytes = mapper.writeValueAsBytes(msg);
                out.writeBytes(bytes);
            } catch (JsonProcessingException e) {
                ctx.fireExceptionCaught(new SerializationException("序列化失败", e));
            }
        }
    }

    /**
     * <p>
     * Decoder 类是一个 Netty 的解码器，用于将字节流反序列化为指定类型的对象。
     * 它继承自 MessageToMessageDecoder，负责将传入的 ByteBuf 中的字节数组反序列化为目标对象。
     * </p>
     */
    public static class Decoder extends MessageToMessageDecoder<ByteBuf> {
        /**
         * <p>
         * 目标对象的类类型，用于指定反序列化的目标类型。
         * </p>
         */
        private final Class<?> targetClass;

        /**
         * <p>
         * 用于执行 JSON 反序列化操作的 ObjectMapper 实例。
         * </p>
         */
        private final ObjectMapper mapper = new ObjectMapper();

        /**
         * <p>
         * 构造函数，用于初始化 Decoder 实例。
         * 接收一个目标类类型作为参数，用于指定反序列化的目标类型。
         * </p>
         *
         * @param targetClass 目标对象的类类型
         */
        public Decoder(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        /**
         * <p>
         * 该方法用于将传入的 ByteBuf 中的字节数组反序列化为目标对象。
         * 根据目标类类型的不同，分别处理 RpcRequest 和 RpcResponse 的反序列化。
         * 对于 RpcRequest，还会对参数类型进行转换。
         * </p>
         *
         * @param ctx 通道处理上下文
         * @param msg 包含字节流的 ByteBuf
         * @param out 用于存储反序列化后的对象的列表
         * @throws Exception 如果反序列化过程中出现异常
         */
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            byte[] bytes = new byte[msg.readableBytes()];
            msg.readBytes(bytes);

            if (targetClass == RpcRequest.class) {
                // 反序列化 RpcRequest
                RpcRequest request = mapper.readValue(bytes, RpcRequest.class);

                // 根据 parameterTypes 转换参数类型
                if (request.getParameterTypes() != null && request.getParameters() != null) {
                    for (int i = 0; i < request.getParameters().length; i++) {
                        Class<?> targetType = request.getParameterTypes()[i];
                        Object param = request.getParameters()[i];
                        // 使用 ObjectMapper 转换类型
                        Object convertedParam = mapper.convertValue(param, targetType);
                        request.getParameters()[i] = convertedParam;
                    }
                }

                out.add(request);
            } else if (targetClass == RpcResponse.class) {
                // 反序列化 RpcResponse
                RpcResponse response = mapper.readValue(bytes, RpcResponse.class);
                if (response.getException() == null) response.setException(null);
                out.add(response);
            }
        }
    }
}
