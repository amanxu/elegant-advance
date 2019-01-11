package com.elegant.netty.enums;

import lombok.Getter;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-05 18:11
 */
@Getter
public enum MsgTypeEnum {

    CHANNEL_REG(100001, "客户端通道注册"),
    EXECUTE_JOB(100002, "执行作业"),
    HEART_CHECK(100003, "心跳检测"),
    TRIGGER_FALL_BACK(100004, "执行器触发回调"),
    EXECUTE_FALL_BACK(100005, "执行器执行回调");

    private Integer msgType;

    private String desc;

    MsgTypeEnum(Integer msgType, String desc) {
        this.msgType = msgType;
        this.desc = desc;
    }

    public static String getDescByType(Integer msgType) {
        for (MsgTypeEnum msgTypeEnum : MsgTypeEnum.values()) {
            if (msgTypeEnum.msgType.equals(msgType)) {
                return msgTypeEnum.desc;
            }
        }
        return null;
    }
}
