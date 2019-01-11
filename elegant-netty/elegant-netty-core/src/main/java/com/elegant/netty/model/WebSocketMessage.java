package com.elegant.netty.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoxu.nie
 */
@Data
public class WebSocketMessage implements Serializable {

    /*** 消息接收帐户*/
    private String account;

    /*** 消息内容*/
    private String content;

    /*** 消息类型,群发|点对点*/
    private String msgType;

}
