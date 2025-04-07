package com.example.rpc.server.core.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * <p>RpcServerInitializer 是 Netty 服务器的通道初始化器。</p>
 * <p>该初始化器负责在每个新连接的 SocketChannel 中添加必要的处理器，以处理 RPC 请求。</p>
 * <p>具体来说，它添加了以下处理器：</p>
 * <ul>
 *     <li><strong>LengthFieldBasedFrameDecoder</strong>: 用于根据消息长度字段解码消息。</li>
 *     <li><strong>LengthFieldPrepender</strong>: 用于在消息前面添加长度字段。</li>
 *     <li><strong>ServerRequestHandler</strong>: 用于处理具体的 RPC 请求。</li>
 * </ul>
 */
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * <p>初始化通道时调用的方法。</p>
     * <p>该方法在每个新连接的 SocketChannel 中添加处理器，以处理 RPC 请求。</p>
     *
     * @param ch 新连接的 SocketChannel
     * @throws Exception 如果初始化过程中发生异常
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4))
                .addLast(new LengthFieldPrepender(4))
                .addLast(new ServerRequestHandler());
    }
}
