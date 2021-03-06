package com.elegant.netty.model;

import com.elegant.netty.enums.ErrorCodeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2019-01-09 13:32
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class ChannelMessage implements Serializable {

    public ChannelMessage(Integer code, String msg, MessageContent messageContent) {
        this.code = code;
        this.msg = msg;
        this.messageContent = messageContent;
    }

    public ChannelMessage(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ChannelMessage(MessageContent messageContent) {
        this.messageContent = messageContent;
    }

    private Integer code;

    private String msg;

    private MessageContent messageContent;

    public static ChannelMessage success() {
        return new ChannelMessage(ErrorCodeEnum.CHANNEL_MSG_SUC.getCode(), ErrorCodeEnum.CHANNEL_MSG_SUC.getMsg());
    }

    public static ChannelMessage success(MessageContent message) {
        ChannelMessage channelMessage = new ChannelMessage(ErrorCodeEnum.CHANNEL_MSG_SUC.getCode(), ErrorCodeEnum.CHANNEL_MSG_SUC.getMsg());
        channelMessage.setMessageContent(message);
        return channelMessage;
    }

    public static ChannelMessage error() {
        return new ChannelMessage(ErrorCodeEnum.CHANNEL_MSG_FAIL.getCode(), ErrorCodeEnum.CHANNEL_MSG_FAIL.getMsg());
    }

    public static ChannelMessage error(MessageContent message) {
        ChannelMessage channelMessage = new ChannelMessage(ErrorCodeEnum.CHANNEL_MSG_FAIL.getCode(), ErrorCodeEnum.CHANNEL_MSG_FAIL.getMsg());
        channelMessage.setMessageContent(message);
        return channelMessage;
    }

}
