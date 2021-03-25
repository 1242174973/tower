package com.tower.enums;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/25 16:11
 */
public enum WelfareTypeEnum {
    //金币
    GOLD(1,""),
    ;
    private int code;
    private String desc;

    WelfareTypeEnum(int code, String desc) {
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
