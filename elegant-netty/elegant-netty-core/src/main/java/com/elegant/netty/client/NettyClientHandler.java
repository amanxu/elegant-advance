package com.elegant.netty.client;

import com.alibaba.fastjson.JSON;
import com.elegant.netty.common.Constant;
import com.elegant.netty.config.NettyClientConfigProperties;
import com.elegant.netty.config.NettyServerConfigProperties;
import com.elegant.netty.enums.ErrorCodeEnum;
import com.elegant.netty.enums.MsgTypeEnum;
import com.elegant.netty.exception.BusinessException;
import com.elegant.netty.model.ChannelMessage;
import com.elegant.netty.model.JobExecuteInfo;
import com.elegant.netty.model.MessageContent;
import com.elegant.netty.proxy.JobDynamicProxyHandler;
import com.elegant.netty.service.IJobService;
import com.elegant.netty.utils.JobServiceBeanUtil;
import com.elegant.netty.utils.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

import static com.elegant.netty.enums.MsgTypeEnum.EXECUTE_JOB;

/**
 * @author xiaoxu.nie
 * @date 2019-01-05
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 捕捉异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("[异常，释放资源] {}", cause.getMessage());
        ctx.close();
        // 重启Channel通道
        NettyServerConfigProperties configProperty = SpringContextUtil.getBean(NettyServerConfigProperties.class);
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(configProperty.getHostIp(), configProperty.getHostPort());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[客户端和服务端通道建立成功，准备注册通道...]");
        MessageContent messageContent = new MessageContent();
        messageContent.setMsgType(MsgTypeEnum.CHANNEL_REG.getMsgType());
        NettyClientConfigProperties configProperty = SpringContextUtil.getBean(NettyClientConfigProperties.class);
        if (configProperty.getExecutorPort() == null) {
            throw new BusinessException(ErrorCodeEnum.EXECUTOR_PORT_CONG_ERR.getMsg());
        }
        //获取本地的IP地址
        InetAddress address = InetAddress.getLocalHost();
        StringBuilder builder = new StringBuilder(address.getHostAddress());
        builder.append(":").append(configProperty.getExecutorPort());
        messageContent.setData(builder.toString());
        ctx.writeAndFlush(new ChannelMessage(messageContent));
    }

    /**
     * 服务端返回应答消息时，调用此方法
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("[客户端接收消息]: {}", JSON.toJSONString(msg));
        if (msg instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) msg;
            parseChannelMsg(ctx, channelMessage.getMessageContent());
        }
    }

    /**
     * 解析通道消息
     *
     * @param ctx
     * @param message
     */
    private void parseChannelMsg(ChannelHandlerContext ctx, MessageContent message) {

        try {
            if (EXECUTE_JOB.getMsgType().equals(message.getMsgType())) {
                // 设置返回消息的类型
                message.setMsgType(MsgTypeEnum.TRIGGER_FALL_BACK.getMsgType());

                // 执行job服务， 触发后返回触发结果，执行使用异步线程，执行成功后回调
                /*jobService.execute(jobExecuteInfo.getExecuteParam());*/
                JobAsyncExecutor jobAsyncExecutor = SpringContextUtil.getBean(JobAsyncExecutor.class);
                jobAsyncExecutor.asyncExecutor(ctx.channel(), message);

                ctx.channel().writeAndFlush(ChannelMessage.success(message));
            }
        } catch (Exception e) {
            // JOB触发失败
            log.error("JOB Executor Err:{}", e);
            ctx.channel().writeAndFlush(ChannelMessage.success(message));
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("{} -> [客户端心跳监测发送] 通道编号：{}", this.getClass().getName(), ctx.channel().id());
        if (evt instanceof IdleStateEvent) {
            MessageContent messageContent = new MessageContent();
            messageContent.setMsgType(MsgTypeEnum.HEART_CHECK.getMsgType());
            messageContent.setData(Constant.HEART_CHECK);
            ctx.writeAndFlush(new ChannelMessage(messageContent));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
