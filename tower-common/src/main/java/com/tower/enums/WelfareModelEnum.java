package com.tower.enums;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/25 16:11
 */
public enum WelfareModelEnum {
    //金币
    SIGN_IN(1, "登录签到"),
    RESCUE(2, "救援金"),
    CHALLENGE(3, "投注返利"),
    REBATE_EXTRACT(4, "返利提现"),
    TRANSFER_WITHHOLD(5, "转账扣款"),
    TRANSFER_AWARD(6, "转账奖励"),
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
