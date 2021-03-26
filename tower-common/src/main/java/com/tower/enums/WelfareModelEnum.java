package com.tower.enums;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/25 16:11
 */
public enum WelfareModelEnum {
    //金币
    SIGN_IN(1,"签到"),
    RESCUE(2,"救援"),
    CHALLENGE(3,"挑战"),
    WITHDRAW(4,"提现"),
    WITHDRAW_ERROR(4,"提现失败"),
    ;
    private int code;
    private String desc;

    WelfareModelEnum(int code, String desc) {
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
