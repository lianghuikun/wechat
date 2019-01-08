package com.wechat.enums;

/**
 * 微信消息类型
 */
public enum MsgTypeEnum {
    TEXT("text", "文本"),
    IMAGE("image", "图片"),
    VOICE("voice", "语音"),
    VIDEO("video", "视频"),
    SHORTVIDEO("shortvideo", "小视频"),
    LOCATION("location", "地理信息"),
    LINK("link", "链接"),
    EVENT("event","事件");
    String code;
    String description;

    MsgTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
