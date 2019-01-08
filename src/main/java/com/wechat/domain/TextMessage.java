package com.wechat.domain;

import lombok.Data;

/**
 * 文本消息工具类
 */
@Data
public class TextMessage extends BaseMessage{

    //文本内容
    private String Content;
    // 消息id，64位整型
    private long MsgId;
}
