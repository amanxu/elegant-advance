package com.elegant.netty.config;

import com.elegant.netty.client.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-06 9:58
 */
@Configuration
@EnableConfigurationProperties({NettyServerConfigProperties.class})
public class NettyClientConfigStarter implements CommandLineRunner {

    @Autowired
    private NettyServerConfigProperties nettyServerConfigProperties;

    @Override
    public void run(String... args) throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(nettyServerConfigProperties.getHostIp(), nettyServerConfigProperties.getHostPort());
    }
}
