package com.elegant.netty.config;

import com.elegant.netty.annotation.JobService;
import com.elegant.netty.utils.JobServiceBeanUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 16:54
 */
@Component
public class JobServiceBeanListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 获取JobService注解的Bean
        Map<String, Object> jobAnnotationBeanMap = event.getApplicationContext().getBeansWithAnnotation(JobService.class);

        JobServiceBeanUtil.putJobBeans(jobAnnotationBeanMap);
    }
}