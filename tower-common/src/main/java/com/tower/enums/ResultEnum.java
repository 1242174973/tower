package com.tower.enums;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/25 16:11
 */
public enum ResultEnum {
    //金币
    NOT_RESULT(1,"未开奖"),
    ALREADY_RESULT(2,"已开奖"),
    REVOCATION(3,"撤销下注"),
    ;
    private int code;
    private String desc;

    ResultEnum(int code, String desc) {
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
