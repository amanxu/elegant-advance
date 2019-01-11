package com.elegant.netty.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 10:08
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JobService {

    String value() default "";
}
