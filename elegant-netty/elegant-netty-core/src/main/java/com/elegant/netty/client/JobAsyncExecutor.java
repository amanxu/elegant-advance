package com.elegant.netty.client;

import com.elegant.netty.common.Result;
import com.elegant.netty.enums.MsgTypeEnum;
import com.elegant.netty.enums.ResultEnum;
import com.elegant.netty.model.ChannelMessage;
import com.elegant.netty.model.JobExecuteInfo;
import com.elegant.netty.model.MessageContent;
import com.elegant.netty.proxy.JobDynamicProxyHandler;
import com.elegant.netty.service.IJobService;
import com.elegant.netty.utils.JobServiceBeanUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-10 14:31
 */
@Slf4j
@Service
public class JobAsyncExecutor {

    @Async
    public void asyncExecutor(Channel channel, MessageContent message) {
        long startTime = System.currentTimeMillis();
        try {
            // 模拟异步耗时
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 设置返回消息类型
        message.setMsgType(MsgTypeEnum.EXECUTE_FALL_BACK.getMsgType());
        JobExecuteInfo jobExecuteInfo = (JobExecuteInfo) message.getData();
        String jobServiceName = jobExecuteInfo.getJobServiceName();
        // 1.获取job服务的bean
        Object bean = JobServiceBeanUtil.getBeanByJobName(jobServiceName);
        if (bean == null) {
            channel.writeAndFlush(ChannelMessage.error(message));
            return;
        }
        // 2.获取动态代理类
        JobDynamicProxyHandler jobDynamicProxyHandler = new JobDynamicProxyHandler();
        IJobService jobService = (IJobService) jobDynamicProxyHandler.createInstance(bean);
        Result result = jobService.execute(jobExecuteInfo.getExecuteParam());

        if (result == null || ResultEnum.FAIL.getCode().equals(result.getCode())) {
            channel.writeAndFlush(ChannelMessage.error(message));
            return;
        }
        // 执行任务耗时可能较长，判断通道是否失效
        if (!channel.isActive()) {
            log.warn("JobAsyncExecutor Netty Channel closed...");
            // TODO 通道被关闭 时是否新建通道或者日志记录
            return;
        }
        // JOB执行成功
        jobExecuteInfo.setExecuteResult("异步返回:{" + startTime + "}->{" + System.currentTimeMillis() + "}");
        ChannelMessage success = ChannelMessage.success(message);
        channel.writeAndFlush(success);
    }
}
