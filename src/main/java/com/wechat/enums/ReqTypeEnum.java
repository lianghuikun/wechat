package com.wechat.enums;

/**
 * 图灵机器人请求枚举类
 */
public enum ReqTypeEnum {
    /**
     * 输入类型:0-文本(默认)、1-图片、2-音频
     */
    TEXT(0, "文本(默认)"),
    PICTURE(1, "图片"),
    MEDIA(2, "音频");
    Integer code;
    String description;

    ReqTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
