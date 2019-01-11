package com.elegant.netty.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 17:36
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "netty.client")
public class NettyClientConfigProperties {

    private Integer executorPort;
}
