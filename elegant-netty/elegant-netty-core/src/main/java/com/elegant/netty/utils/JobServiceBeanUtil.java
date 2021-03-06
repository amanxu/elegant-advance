package com.elegant.netty.utils;

import com.elegant.netty.annotation.JobService;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 17:05
 */
public class JobServiceBeanUtil {

    private static Map<String, Object> jobAnnotationBeanMap = Maps.newHashMap();

    /**
     * 初始化JobService的bean
     *
     * @param jobAnnotationBeans
     */
    public static void putJobBeans(Map<String, Object> jobAnnotationBeans) {
        jobAnnotationBeans.forEach((k, v) -> {
            JobService annotation = v.getClass().getAnnotation(JobService.class);
            String annotationValue = annotation.value();
            jobAnnotationBeanMap.put(annotationValue, v);
        });
    }

    /**
     * 根据作业名称获取对应的Bean
     *
     * @param jobName
     * @return
     */
    public static Object getBeanByJobName(String jobName) {
        return jobAnnotationBeanMap.get(jobName);
    }

}
