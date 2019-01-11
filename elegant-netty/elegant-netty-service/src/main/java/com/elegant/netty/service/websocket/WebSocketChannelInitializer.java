package com.elegant.netty.service.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-06 12:15
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //HttpServerCodec: webSocket协议本身是基于http协议的,针对http协议进行编解码
        pipeline.addLast("httpServerCodec", new HttpServerCodec());

        // ChunkedWriteHandler以块的方式来写的处理器,文件过大会将内存撑爆
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());

        // 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，该Handler必须放在HttpServerCodec后的后面，netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));

        // "/ws" 表示该处理器处理的webSocketPath的路径，例如 客户端连接时使用：ws://127.0.0.1/ws 才能被这个处理器处理，反之则不行
        /*pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws/*"));*/

        // 添加心跳支持
        ch.pipeline().addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("myWebSocketHandler", new WebSocketHandler());
    }
}