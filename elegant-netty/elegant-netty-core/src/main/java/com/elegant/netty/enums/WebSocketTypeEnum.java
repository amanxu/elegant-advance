package com.elegant.netty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-06 14:27
 */
@Getter
@AllArgsConstructor
public enum WebSocketTypeEnum {
    PTP("PTP", "点对点通知"),
    BROADCAST("BROADCAST", "广播通知"),
    CHANNEL_REG("CHANNEL_REG", "客户端通道注册");

    private String msgType;

    private String desc;
}
