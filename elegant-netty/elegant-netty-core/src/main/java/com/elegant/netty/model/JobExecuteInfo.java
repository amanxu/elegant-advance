package com.elegant.netty.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 13:43
 */
@Setter
@Getter
@ToString
public class JobExecuteInfo implements Serializable {

    /**
     * 作业执行日志ID
     */
    private Integer jobLogId;

    /**
     * JOB的id标识
     */
    private Integer jobId;

    /**
     * job服务的名称
     */
    private String jobServiceName;

    /**
     * job执行时接收的参数
     */
    private String executeParam;

    /**
     * 作业触发结果
     */
    private String triggerResult;

    /**
     * 作业执行结果
     */
    private String executeResult;

}
