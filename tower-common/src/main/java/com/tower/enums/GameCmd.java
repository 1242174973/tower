package com.tower.enums;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/30 14:33
 */
public enum GameCmd {
    //xxx
    NULL(0, "null"),
    GAME_START(1, "游戏开始"),
    UPCOMING_AWARD(2, "怪物即将进攻"),
    AWARD(3, "怪物进攻"),
    BET(4,"下注"),
    REPETITION_BET(5,"重复下注"),
    BET_INFO(6,"下注信息"),
    ;
    private Integer code;

    private String msg;

    GameCmd(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static GameCmd parseCode(int cmd) {
        for (GameCmd value : values()) {
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
