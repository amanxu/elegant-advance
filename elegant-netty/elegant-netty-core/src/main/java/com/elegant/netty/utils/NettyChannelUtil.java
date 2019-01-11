package com.elegant.netty.utils;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentMap;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 12:14
 */
@Slf4j
public class NettyChannelUtil {

    /**
     * 主机对应的通道
     */
    private static final ConcurrentMap<String, Channel> channelMap = Maps.newConcurrentMap();

    /**
     * 通道加入Map
     *
     * @param host
     * @param channel
     */
    public static void putChannel(String host, Channel channel) {
        channelMap.put(host, channel);
    }

    /**
     * 根据主机获取Channel
     *
     * @param host
     * @return
     */
    public static Channel getChannelByHost(String host) {
        if (StringUtils.isBlank(host)) {
            return null;
        }
        return channelMap.get(host);
    }

    /**
     * 移除主机通道
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        channelMap.forEach((k, v) -> {
            if (channel.equals(v)) {
                channelMap.replace(k, v);
            }
        });
    }

    /**
     * 根据key移除主机通道
     *
     * @param host
     */
    public static void removeChannel(String host) {
        channelMap.remove(host);
    }

}
