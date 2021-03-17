
package com.tower.core.constant;

/**
 * @author Administrator
 */
public interface Mid {
    ///////////////////////////////////////////////////////
    ///////////可共用部分
    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    /**
     * 心跳相关请求
     */
    int MID_HEART_BEAT_REQ = 1000;
    /**
     * 登录相关请求
     */
    int MID_LOGIN_REQ = 1001;
    /**
     * 游戏相关请求
     */
    int MID_GAME_REQ = 1002;
    /**
     * 记录相关请求
     */
    int MID_RECORD_REQ = 1003;
    /**
     * 心跳相关请求
     */
    int MID_HEART_BEAT_RES = 2000;
    /**
     * 登录相关请求返回
     */
    int MID_LOGIN_RES = 2001;
    /**
     * 游戏相关请求返回
     */
    int MID_GAME_RES = 2002;
    /**
     * 记录相关请求返回
     */
    int MID_RECORD_RES = 2003;
    /**
     * 错误信息返回
     */
    int MID_SERVER_ERROR_RES = 2100;

}
