package com.elegant.netty.service.comfig;

import com.elegant.netty.config.NettyServerConfigProperties;
import com.elegant.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-06 9:58
 */
@Configuration
@EnableConfigurationProperties({NettyServerConfigProperties.class})
public class NettyServerConfigStarter implements CommandLineRunner {

    @Autowired
    private NettyServerConfigProperties nettyConfigProperty;

    @Override
    public void run(String... args) throws Exception {
        NettyServer nettyServer = new NettyServer();
        nettyServer.bind(nettyConfigProperty.getHostPort());
    }
}
