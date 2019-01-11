package com.elegant.netty.service.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-06 12:39
 */
@Slf4j
public class WebSocketUsers {

    private static final ConcurrentMap<String, Channel> userChannelMap = PlatformDependent.newConcurrentHashMap();

    private static WebSocketUsers ourInstance;

    private WebSocketUsers() {
    }

    public static WebSocketUsers getInstance() {
        if (ourInstance == null) {
            synchronized (WebSocketUsers.class) {
                if (ourInstance == null) {
                    ourInstance = new WebSocketUsers();
                }
            }
        }
        return ourInstance;
    }

    /**
     * 存储通道
     *
     * @param key     唯一键
     * @param channel 通道
     */
    public static void put(String key, Channel channel) {
        userChannelMap.put(key, channel);
    }

    /**
     * 移除通道
     *
     * @param channel 通道
     * @return 移除结果
     */
    public static boolean remove(Channel channel) {
        String key = null;
        boolean b = userChannelMap.containsValue(channel);
        if (b) {
            Set<Map.Entry<String, Channel>> entries = userChannelMap.entrySet();
            for (Map.Entry<String, Channel> entry : entries) {
                Channel value = entry.getValue();
                if (value.equals(channel)) {
                    key = entry.getKey();
                    break;
                }
            }
        } else {
            return true;
        }
        return remove(key);
    }

    /**
     * 移出通道
     *
     * @param key 键
     */
    public static boolean remove(String key) {
        Channel remove = userChannelMap.remove(key);
        boolean containsValue = userChannelMap.containsValue(remove);
        log.info("[通道移出结果]: {}", containsValue ? "失败" : "成功");
        return containsValue;
    }

    /**
     * 获取在线用户列表
     *
     * @return 返回用户集合
     */
    public static ConcurrentMap<String, Channel> getUserChannelMap() {
        return userChannelMap;
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    public static void sendMessageToUsers(String message) {
        Collection<Channel> values = userChannelMap.values();
        for (Channel value : values) {
            value.write(new TextWebSocketFrame(message));
            value.flush();
        }
    }

    /**
     * 给某个人发送消息
     *
     * @param userName key
     * @param message  消息
     */
    public static void sendMessageToUser(String userName, String message) {
        Channel channel = userChannelMap.get(userName);
        channel.write(new TextWebSocketFrame(message));
        channel.flush();
    }
}
