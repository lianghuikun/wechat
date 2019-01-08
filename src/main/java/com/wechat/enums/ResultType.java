package com.wechat.enums;

/**
 * 图灵机器人返回结果
 */
public enum ResultType {
    TEXT("TEXT", "文本"),
    URL("URL", "连接"),
    VOICE("VOICE", "音频"),
    VIDEO("VIDEO", "视频"),
    IMAGE("IMAGE", " 图片"),
    NEWS("NEWS", "图文");
    String code;
    String description;

    ResultType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
