package com.platform.modules.chat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 消息状态
 */
@Getter
public enum MsgStatusEnum {

    /**
     * 正常
     */
    NORMAL("0", "正常"),
    /**
     * 对方不是自己朋友
     */
    FRIEND_TO("1", "对方不是你的好友，消息发送失败"),
    /**
     * 自己不是对方朋友
     */
    FRIEND_FROM("2", "你不是对方的好友，消息发送失败"),
    /**
     * 黑名单
     */
    FRIEND_BLACK("3", "消息已发出，但被对方拒收了"),
    /**
     * 注销
     */
    FRIEND_DELETED("4", "对方已注销，消息发送失败"),
    /**
     * 群不存在
     */
    GROUP_NOT_EXIST("5", "当前群不存在，消息发送失败"),
    /**
     * 群明细不存在
     */
    GROUP_INFO_NOT_EXIST("6", "你不在当前群中，消息发送失败"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    MsgStatusEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
