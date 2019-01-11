package com.elegant.netty.service.schedule;

import com.elegant.netty.enums.MsgTypeEnum;
import com.elegant.netty.model.ChannelMessage;
import com.elegant.netty.model.JobExecuteInfo;
import com.elegant.netty.model.MessageContent;
import com.elegant.netty.utils.NettyChannelUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 12:07
 */
@Slf4j
@Service
public class JobScheduleService {

    private static final String hostName = "192.168.62.236:8080";
    private static final String emailJob = "emailSendJobService";
    private static final String smsJob = "smsSendJobHandler";

    /**
     * 定时任务，模拟调度中心出发job执行
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void jobTrigger() {
        // 0 */2 * * * ? 没隔两分钟执行一次
        log.info("开始触发任务调度......");
        Channel channel = NettyChannelUtil.getChannelByHost(hostName);
        if (channel == null) {
            return;
        }
        MessageContent messageEmail = new MessageContent();
        messageEmail.setMsgType(MsgTypeEnum.EXECUTE_JOB.getMsgType());
        JobExecuteInfo jobExecuteInfo = new JobExecuteInfo();
        jobExecuteInfo.setJobServiceName(emailJob);
        jobExecuteInfo.setExecuteParam("{\"jobName\":\"emailJob\"}");
        messageEmail.setData(jobExecuteInfo);
        channel.writeAndFlush(new ChannelMessage(messageEmail));
    }
}
