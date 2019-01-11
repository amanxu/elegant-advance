package com.elegant.netty.annotation;

import com.elegant.netty.config.JobServiceBeanListener;
import com.elegant.netty.config.NettyClientConfigProperties;
import com.elegant.netty.config.NettyClientConfigStarter;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 18:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAsync
@Import({NettyClientConfigProperties.class, NettyClientConfigStarter.class, JobServiceBeanListener.class})
public @interface EnableJobService {
}
