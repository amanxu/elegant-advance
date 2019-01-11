package com.elegant.netty.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-10 17:30
 */
@Setter
@Getter
@ToString
public class MessageContent implements Serializable {

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 消息内容
     */
    private Object data;
}
