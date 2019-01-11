package com.elegant.netty.server;

import com.elegant.netty.enums.MsgTypeEnum;
import com.elegant.netty.model.ChannelMessage;
import com.elegant.netty.model.MessageContent;
import com.elegant.netty.utils.NettyChannelUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoxu.nie
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private int lossConnectCount = 0;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 读事件
     *
     * @param ctx ChannelHandlerContext
     * @param msg 消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) msg;
            parseChannelMsg(ctx, channelMessage);
        }
    }

    /**
     * 解析channel的消息
     *
     * @param ctx
     * @param channelMessage
     */
    private void parseChannelMsg(ChannelHandlerContext ctx, ChannelMessage channelMessage) {
        MessageContent message = channelMessage.getMessageContent();
        // 心跳消息
        if (MsgTypeEnum.HEART_CHECK.getMsgType().equals(message.getMsgType())) {
            log.info("{} -> [心跳监测] {}：通道活跃", this.getClass().getName(), ctx.channel().id());
            lossConnectCount = 0;
            // 通道注册
        } else if (MsgTypeEnum.CHANNEL_REG.getMsgType().equals(message.getMsgType())) {
            String hostName = (String) message.getData();
            NettyChannelUtil.putChannel(hostName, ctx.channel());
            log.info("执行器通道注册:{}", hostName);
        } else if (MsgTypeEnum.TRIGGER_FALL_BACK.getMsgType().equals(message.getMsgType())) {
            log.info("执行器JOB触发结果回调:{};{}", channelMessage.getCode(),channelMessage.getMsg());

        } else if (MsgTypeEnum.EXECUTE_FALL_BACK.getMsgType().equals(message.getMsgType())) {
            log.info("执行器JOB执行结果回调:{};{}", channelMessage.getCode(),channelMessage.getMsg());

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    /**
     * 触发器
     *
     * @param channelHandlerContext channelHandlerContext
     * @param evt
     * @throws Exception exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                lossConnectCount++;
                if (lossConnectCount > 2) {
                    log.info("{} -> [心跳检测超时，释放不活跃通道] {}", this.getClass().getName(), channelHandlerContext.channel().id());
                    channelHandlerContext.channel().close();
                    // 移除通道
                    NettyChannelUtil.removeChannel(channelHandlerContext.channel());
                }
            }
        } else {
            super.userEventTriggered(channelHandlerContext, evt);
        }
    }
}
