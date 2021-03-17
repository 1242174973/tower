package com.tower.enums;

public enum CheckSMSCode {
    NULL(0, "未知错误"),
    SUCCESS(1, "成功"),
    ERROR(2, "错误"),
    EXPIRED(3, "验证码过期"),
    CHECK_LIMIT(4, "验证次数达到上限");
    private int code;
    private String desc;

    CheckSMSCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
