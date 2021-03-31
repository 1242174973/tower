package com.tower.core.constant;


import com.tower.msg.Tower;

/**
 * @author Administrator
 */
public class MidTypeUtil implements Mid {
	public static boolean isLogin(Tower.MsgCtn msg){
        return msg.getType() == Mid.MID_LOGIN_REQ;
    }

	public static boolean isGame(Tower.MsgCtn msg){
        return msg.getType() == Mid.MID_GAME_REQ;
    }



    public static boolean isRecord(Tower.MsgCtn msg) {
        return msg.getType() == Mid.MID_ROOM_REQ;
    }
    public static boolean isRoom(Tower.MsgCtn msg) {
        return msg.getType() == Mid.MID_RECORD_REQ;
    }


    public static boolean isHeartBeat(Tower.MsgCtn msg) {
        return msg.getType() == Mid.MID_HEART_BEAT_REQ;
    }
}
