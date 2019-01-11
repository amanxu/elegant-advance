package com.elegant.netty.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 17:36
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "netty.server")
public class NettyServerConfigProperties {

    private String hostIp;

    private Integer hostPort;

}
