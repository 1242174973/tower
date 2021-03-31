package com.tower.core.constant;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/30 14:33
 */
public enum GameStatus {
    //xxx
    NULL(0, "null"),
    GAME_START(1, "游戏开始"),
    UPCOMING_AWARD(2, "怪物即将进攻"),
    AWARD(3, "怪物进攻"),
    ;
    private Integer code;

    private String msg;

    GameStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static GameStatus parseCode(int cmd) {
        for (GameStatus value : values()) {
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
