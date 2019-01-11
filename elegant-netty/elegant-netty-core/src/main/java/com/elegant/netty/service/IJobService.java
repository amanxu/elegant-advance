package com.elegant.netty.service;

import com.elegant.netty.common.Result;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 11:55
 */
public interface IJobService {

    /**
     * 执行器执行任务方法
     *
     * @param param
     * @return
     */
    Result execute(String param);
}
