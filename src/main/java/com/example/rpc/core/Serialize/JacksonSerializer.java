package com.example.rpc.core.Serialize;
import com.example.rpc.core.exception.SerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.util.List;

/**
 * <p>JacksonSerializer 类实现了 RpcSerializer 接口，使用 Jackson 库进行对象的序列化和反序列化。</p>
 * <p>该类提供了将对象转换为字节数组（序列化）和将字节数组转换回对象（反序列化）的方法。</p>
 *
 * <p>核心功能包括：</p>
 * <ul>
 *     <li>serialize 方法：将对象序列化为字节数组。</li>
 *     <li>deserialize 方法：将字节数组反序列化为指定类型的对象。</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * JacksonSerializer serializer = new JacksonSerializer();
 * byte[] serializedBytes = serializer.serialize(myObject);
 * MyObject deserializedObject = serializer.deserialize(serializedBytes, MyObject.class);
 * </pre>
 *
 * <p>注意事项：</p>
 * <ul>
 *     <li>如果序列化或反序列化过程中发生异常，会抛出 RuntimeException。</li>
 *     <li>该类依赖于 Jackson 库，确保项目中已正确引入该库。</li>
 * </ul>
 */
public class JacksonSerializer  implements RpcSerializer
{
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) {
        try{
            return mapper.writeValueAsBytes(object);
        }catch (Exception e){
            throw new RuntimeException("序列化失败",e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException("反序列化失败",e);
        }
    }

    // 新增 Netty 处理器实现
    public static class Encoder extends MessageToByteEncoder<Object> {
        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception{
            try {
                byte[] bytes = mapper.writeValueAsBytes(msg);
                out.writeBytes(bytes);
            } catch (JsonProcessingException e) {
                ctx.fireExceptionCaught(new SerializationException("序列化失败", e));
            }
        }

    }

    public static class Decoder extends ByteToMessageDecoder {
        private final Class<?> targetClass;

        public Decoder(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            try {
                byte[] bytes = new byte[in.readableBytes()];
                in.readBytes(bytes);
                Object obj = mapper.readValue(bytes, targetClass);
                out.add(obj);
            } catch (IOException e) {
                ctx.fireExceptionCaught(new SerializationException("反序列化失败", e));
            }
        }

    }
}