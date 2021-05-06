package com.tower.enums;

/**
 * @author xxxx
 * @date 2021/3/25 16:17
 */
public enum AwardStatus {
    //未结算
    NOT_CLEARING(0,"未结算"),
    CLEARING(1,"已结算"),
    GET(2,"已领取"),
    ;
    private int code;
    private String desc;

    AwardStatus(int code, String desc) {
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
