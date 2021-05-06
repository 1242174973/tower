package com.tower.enums;

/**
 * @author xxxx
 * @date 2021/3/30 14:33
 */
public enum RecordCmd {
    //xxx
    NULL(0, "null"),
    ROUGH_ATTACK_LOG(1, "粗略的进攻记录"),
    DETAILED_ATTACK_LOG(2, "详细的进攻记录"),
    BET_LOG(3, "下注记录"),
    ;
    private Integer code;

    private String msg;

    RecordCmd(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static RecordCmd parseCode(int cmd) {
        for (RecordCmd value : values()) {
            if (value.getCode().equals(cmd)) {
                return value;
            }
        }
        return NULL;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
