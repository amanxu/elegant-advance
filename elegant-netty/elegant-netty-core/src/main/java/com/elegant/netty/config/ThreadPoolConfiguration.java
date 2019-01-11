package com.elegant.netty.config;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "thread.pool")
@Slf4j
public class ThreadPoolConfiguration implements AsyncConfigurer {

    /**
     * 核心线程数 corePoolSize = 每秒任务数/(1s/ 每个任务花费时间) = 每秒任务数*每个任务花费时间
     */
    private int corePoolSize = 20;

    /**
     * 最大线程数
     */
    private int maxPoolSize = 30;

    /**
     * 队列大小 queueCapacity = (coreSizePool / 每个任务花费时间) * 系统允许容忍的最大响应时间
     */
    private int queueCapacity = 600;

    /**
     * 存活时间
     */
    private int keepAliveSeconds = 60000;

    /**
     * 线程池中的线程的名称前缀
     */
    private String threadNamePrefix = "shengxue-threadPool-";

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置存活时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //等待任务在在关机时执行 -- 表明等待所有线程执行完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            log.error("Thread Exception message : {}" , throwable.getMessage());
            log.error("Method name : {}" , method.getName());
            for (Object param:objects) {
                log.error("Parameter value : {}" , param);
            }
        }
    }
}
