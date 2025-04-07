// ClientChannelInitializer.java
package com.example.rpc.client.handler;

import com.example.rpc.core.Serialize.JacksonSerializer;
import com.example.rpc.core.model.RpcRequest;
import com.example.rpc.core.model.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 *  {@code ClientChannelInitializer} 类是 Netty 客户端管道初始化器，负责初始化客户端的 Netty 管道。
 *  本类继承自 {@link ChannelInitializer}，用于在客户端连接建立时初始化管道，添加解码器、编码器和业务处理器。
 *  <p> 该类的主要功能是配置客户端的 Netty 管道，包括添加解码器、编码器和业务处理器，以便客户端能够正确地处理 RPC 请求和响应。
 *  <p> 本类中的 {@code initChannel} 方法用于初始化管道，添加以下处理器：
 *  <ul>
 *      <li> {@link LengthFieldBasedFrameDecoder}：用于解码入站数据，根据长度字段进行拆包。</li>
 *      <li> {@link JacksonSerializer.Decoder}：用于解码入站数据，将字节数组转换为 {@link RpcResponse} 对象。</li>
 *      <li> {@link LengthFieldPrepender}：用于编码出站数据，添加长度字段。</li>
 *      <li> {@link JacksonSerializer.Encoder}：用于编码出站数据，将对象转换为字节数组。</li>
 *      <li> {@link ClientResponseHandler}：用于处理 RPC 响应，并调用对应的 Future 对象。</li>
 *  </ul>
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                // 入站处理（解码）
                .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4))
                .addLast(new JacksonSerializer.Decoder(RpcResponse.class))

                // 出站处理（编码）
                .addLast(new LengthFieldPrepender(4))
                .addLast(new JacksonSerializer.Encoder())

                // 业务处理器
                .addLast(new ClientResponseHandler());
    }
}