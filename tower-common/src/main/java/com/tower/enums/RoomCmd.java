package com.tower.enums;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/30 14:33
 */
public enum RoomCmd {
    //xxx
    NULL(0, "null"),
    ENTER_ROOM(1, "进入房间"),
    EXIT_ROOM(2, "退出房间"),
    ROOM_INFO(3, "房间信息,重连使用"),
    ;
    private Integer code;

    private String msg;

    RoomCmd(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static RoomCmd parseCode(int cmd) {
        for (RoomCmd value : values()) {
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
