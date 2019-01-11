package com.elegant.netty.client.service;

import com.elegant.netty.annotation.JobService;
import com.elegant.netty.common.Result;
import com.elegant.netty.service.IJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2018-08-10 14:42
 */
@Slf4j
@Service
@JobService(value = "emailSendJobService")
public class EmailSendJobService implements IJobService {

    @Override
    public Result execute(String param) {
        log.info("定时（邮件）任务调度...");
        return Result.success();
    }
}
