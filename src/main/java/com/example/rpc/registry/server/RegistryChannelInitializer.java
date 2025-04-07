package com.example.rpc.registry.server;

import com.example.rpc.registry.handler.RegistryRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

/**
 * <p>RegistryChannelInitializer 类的主要作用是对 SocketChannel 的管道进行初始化操作，
 * 并向该管道中添加用于处理 HTTP 请求的处理器。此类继承自 ChannelInitializer，
 * 在初始化阶段会为 SocketChannel 的管道依次添加 HttpServerCodec、HttpObjectAggregator
 * 和 RegistryRequestHandler 这三个处理器，以完成 HTTP 请求的处理流程。</p>
 *
 * <p>核心功能详细说明：</p>
 * <p>1. 初始化 SocketChannel 的管道：为后续添加处理器以及处理网络数据做准备。</p>
 * <p>2. 添加 HttpServerCodec 处理器：该处理器具备对 HTTP 消息进行解码和编码的能力。
 * 解码是将接收到的二进制数据转换为 HTTP 消息对象，编码则是将 HTTP 消息对象转换为二进制数据进行发送。</p>
 * <p>3. 添加 HttpObjectAggregator 处理器：它的主要任务是把多个分散的 HTTP 消息片段聚合为完整的请求或响应。
 * 这样做可以简化后续处理器对 HTTP 消息的处理逻辑，避免处理不完整的消息。</p>
 * <p>4. 添加 RegistryRequestHandler 处理器：专门用于处理注册请求，对经过前面处理器处理后的 HTTP 请求进行业务逻辑处理。</p>
 *
 * <p>使用示例：</p>
 * <p>以下代码展示了如何创建 RegistryChannelInitializer 实例并对 SocketChannel 进行初始化。</p>
 * <pre>
 * RegistryChannelInitializer initializer = new RegistryChannelInitializer();
 * ChannelPipeline pipeline = new DefaultChannelPipeline();
 * // 注意：此处需要传入有效的 SocketChannel 实例
 * initializer.initChannel(new SocketChannel());
 * </pre>
 *
 * <p>构造函数说明：</p>
 * <p>该类的构造函数不接受任何参数，可直接使用无参构造函数创建实例。</p>
 *
 * <p>使用限制与潜在副作用：</p>
 * <p>1. 该类必须在 ChannelPipeline 中使用才能生效。因为其核心功能是向管道中添加处理器，
 * 若不在管道环境中，处理器无法发挥作用。</p>
 * <p>2. 调用 initChannel 方法时，传入的参数必须是 SocketChannel 类型的对象，
 * 否则可能会导致类型不匹配的异常，影响程序的正常运行。</p>
 */
public class RegistryChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new RegistryRequestHandler());
    }
}