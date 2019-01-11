package com.elegant.netty.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.elegant.netty.enums.WebSocketTypeEnum;
import com.elegant.netty.model.WebSocketMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-06 12:16
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker socketServerHandShaker;
    private int lossConnectCount = 0;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(("[新增连接通道]:{}" + ctx.channel().id().asLongText()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("[通道下线]:{}", ctx.channel().id().asLongText());
        WebSocketUsers.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // http接入,首先使用http建立握手连接
        if (msg instanceof FullHttpRequest) {
            handlerHttpRequest(ctx, (FullHttpRequest) msg);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("消费者WS连接建立成功"));
            // WebSocket消息接入
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 握手请求处理，可以携带用户参数等信息
     *
     * @param channelHandlerContext channelHandlerContext
     * @param req                   请求
     */
    private void handlerHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) {

        // 构造握手响应返回
        String uri = req.uri();
        String webSocketURL = "ws://" + req.headers().get(HttpHeaderNames.HOST) + req.uri();
        WebSocketServerHandshakerFactory wsFactory
                = new WebSocketServerHandshakerFactory(webSocketURL,
                null, false);
        String userName = uri.substring(uri.lastIndexOf("/") + 1);
        // TODO 加入在线用户，自动分配客服，客服不在线时提示非工作时间，一对多聊天
        WebSocketUsers.put(userName, channelHandlerContext.channel());
        socketServerHandShaker = wsFactory.newHandshaker(req);
        if (socketServerHandShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
        } else {
            socketServerHandShaker.handshake(channelHandlerContext.channel(), req);
        }
    }

    /**
     * webSocket处理逻辑
     *
     * @param channelHandlerContext channelHandlerContext
     * @param frame                 webSocketFrame
     */
    private void handlerWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame) throws IOException {
        Channel channel = channelHandlerContext.channel();
        // region 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            log.info("[关闭与客户端的链接]...");
            socketServerHandShaker.close(channel, (CloseWebSocketFrame) frame.retain());
            return;
        }
        // region 判断是否是ping消息
        if (frame instanceof PingWebSocketFrame) {
            log.info("[Ping消息]...");
            channel.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // TextWebSocket
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            log.info("[服务端接收消息]：{}", text);
            // 解析socket消息
            parseSocketMsg(text);
        }
        // 非文本消息处理方式
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s 暂不支持非文本消息", frame.getClass().getName()
            ));
        }
    }

    /**
     * 解析webSocket消息
     *
     * @param msg
     */
    private void parseSocketMsg(String msg) {
        WebSocketMessage webSocketMessage = JSONObject.parseObject(msg, WebSocketMessage.class);

        // 点对点通信,消费者发给客服
        if (WebSocketTypeEnum.PTP.getMsgType().equals(webSocketMessage.getMsgType())) {
            String accept = webSocketMessage.getAccount();
            WebSocketUsers.sendMessageToUser(accept, webSocketMessage.getContent());
            // 广播通信
        } else if (WebSocketTypeEnum.BROADCAST.getMsgType().equals(webSocketMessage.getMsgType())) {
            log.info("[广播消息]：{}", webSocketMessage.getContent());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[通道不活跃下线]:{}", ctx.channel().id().asLongText());
        WebSocketUsers.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                lossConnectCount++;
                if (lossConnectCount == 2) {
                    String warnMsg = "您已1分钟没有发送消息了，长时间不发送消息，系统将断开连接!";
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(warnMsg));
                } else if (lossConnectCount > 2) {
                    log.info("{} -> [释放不活跃通道] {}", this.getClass().getName(), ctx.channel().id());
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}