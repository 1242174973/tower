package com.tower.core.constant;


import com.tower.msg.Tower;

/**
 * @author Administrator
 */
public class MidTypeUtil implements Mid {
	public static boolean isLogin(Tower.MsgCtn msg){
		int type = msg.getType();
		if (type==Mid.MID_LOGIN_REQ) {
			return true;
		}
		return false;
	}

	public static boolean isGame(Tower.MsgCtn msg){
		int type = msg.getType();
		if (type==Mid.MID_GAME_REQ) {
			return true;
		}
		return false;
	}



    public static boolean isRecord(Tower.MsgCtn msg) {
        int type = msg.getType();
        if (type==Mid.MID_RECORD_REQ) {
            return true;
        }
        return false;
    }

    public static boolean isHeartBeat(Tower.MsgCtn msg) {
        int type = msg.getType();
        if (type==Mid.MID_HEART_BEAT_REQ) {
            return true;
        }
        return false;
    }
}
