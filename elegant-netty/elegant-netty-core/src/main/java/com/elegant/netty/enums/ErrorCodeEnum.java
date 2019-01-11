package com.elegant.netty.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * @description:
 * @author: xiaoxu.nie
 * @date: 2018-08-29 11:10
 */
@Getter
public enum ErrorCodeEnum implements Serializable {

    CHANNEL_MSG_SUC(0, "成功"),
    CHANNEL_MSG_FAIL(-1, "失败"),

    JOB_NOT_EXIST_ERR(100001, "作业不存在"),
    JOB_TRIGGER_SUC_ERR(100002, "作业触发成功"),
    JOB_TRIGGER_FAIL_ERR(100003, "作业触发失败"),
    JOB_EXECUTE_SUC_ERR(100004, "作业执行成功"),
    JOB_EXECUTE_FAIL_ERR(100005, "作业执行失败"),
    EXECUTOR_PORT_CONG_ERR(100006, "执行器端口配置错误"),;

    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsgByCode(Integer code) {
        for (ErrorCodeEnum errorCodeEnum : ErrorCodeEnum.values()) {
            if (errorCodeEnum.code.equals(code)) {
                return errorCodeEnum.getMsg();
            }
        }
        return null;
    }
}
