package com.tower.enums;

/**
 * @Author: xxxx
 * @Date: 2021/2/16 14:39
 * @Version 1.0
 */
public enum LikedStatusEnum {
    //ss
    LIKE(1, "点赞"),
    UNLIKE(0, "取消点赞/未点赞");

    private Integer code;

    private String msg;

    LikedStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
