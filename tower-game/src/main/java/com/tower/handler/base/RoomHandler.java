package com.tower.handler.base;

import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.Mid;
import com.tower.core.game.TowerGame;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.MsgUtil;
import com.tower.entity.AttackLog;
import com.tower.entity.Monster;
import com.tower.enums.RoomCmd;
import com.tower.game.MonsterInfo;
import com.tower.msg.Tower;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xxxx
 * @date 2021/3/30 11:36
 */
@Component
@Slf4j
public class RoomHandler extends AbsLogicHandler<Tower.RoomReq> implements Mid, ILogicHandler<Tower.RoomReq> {

    @Resource
    private TowerGame towerGame;

    @Override
    public int getMid() {
        return MID_ROOM_REQ;
    }

    @Override
    public void handle(Tower.RoomReq reqMsg, Channel ch, Long userId) {
        RoomCmd roomCmd = RoomCmd.parseCode(reqMsg.getCmd());
        switch (roomCmd) {
            case ENTER_ROOM:
                enterRoom(userId.intValue());
                towerGame.sendStatus(userId.intValue());
                towerGame.sendBetInfos(userId.intValue());
                break;
            case EXIT_ROOM:
                exitRoom(userId.intValue());
                break;
            case ROOM_INFO:
                roomInfo(userId.intValue());
                towerGame.sendStatus(userId.intValue());
                towerGame.sendBetInfos(userId.intValue());
                break;
            default:
                log.error("未知指令{}",roomCmd.getCode());
                break;
        }
    }

    /**
     * 进入房间
     */
    private void enterRoom(int userId) {
        MsgBossHandler.getRoomUserIds().add(userId);
        Tower.RoomRes.Builder roomRes = getRoomRes();
        roomRes.setCmd(RoomCmd.ENTER_ROOM.getCode());
        roomRes.setSuc(true);
        roomRes.setMsg("加入成功");
        sendMsg(userId, roomRes);
    }

    /**
     * 退出房间
     */
    private void exitRoom(int userId) {
        MsgBossHandler.getRoomUserIds().remove(userId);
        Tower.RoomRes.Builder roomRes = Tower.RoomRes.newBuilder();
        roomRes.setCmd(RoomCmd.EXIT_ROOM.getCode());
        roomRes.setSuc(true);
        roomRes.setMsg("退出成功");
        sendMsg(userId, roomRes);
    }

    /**
     * 房间信息
     */
    private void roomInfo(int userId) {
        Tower.RoomRes.Builder roomRes = getRoomRes();
        roomRes.setCmd(RoomCmd.ROOM_INFO.getCode());
        roomRes.setSuc(true);
        roomRes.setMsg("获取房间信息成功");
        sendMsg(userId, roomRes);
    }

    /**
     * 获得房间返回
     *
     * @return 房间返回信息
     */
    private Tower.RoomRes.Builder getRoomRes() {
        Tower.RoomRes.Builder roomRes = Tower.RoomRes.newBuilder();
        roomRes.addAllMonsterInfo(getMonsterInfoList());
        roomRes.setAttackPageLog(getAttackLogPageList());
        roomRes.addAllRecommendMonster(towerGame.getRecommendMonster());
        roomRes.addAllRecommendId(towerGame.getRecommendIds());
        return roomRes;
    }

    /**
     * 获得记录信息
     *
     * @return 返回给客户端的记录信息
     */
    private Tower.AttackPageLog getAttackLogPageList() {
        //查询最近的100条记录
        int page = towerGame.getAttackLogList().size();
        int size = 100;
        page /= size;
        page += 1;
        List<AttackLog> attackLogs = towerGame.getPageAttackLog(page, size);
        List<Tower.AttackLog> logList = new ArrayList<>();
        for (AttackLog attackLog : attackLogs) {
            Tower.AttackLog.Builder log = Tower.AttackLog.newBuilder();
            log.setMonsterId(attackLog.getMonsterId()==null?0:attackLog.getMonsterId());
            log.setOrderId(attackLog.getOrderId());
            logList.add(log.build());
        }
        Tower.AttackPageLog.Builder pageLog = Tower.AttackPageLog.newBuilder();
        pageLog.setPage(page);
        pageLog.setSize(size);
        pageLog.addAllAttackLog(logList);
        return pageLog.build();
    }

    /**
     * 获得怪物信息
     *
     * @return 返回给客户端的怪物信息
     */
    private List<Tower.MonsterInfo> getMonsterInfoList() {
        List<MonsterInfo> monsterInfoList = towerGame.getMonsterInfoList();
        List<Tower.MonsterInfo> infoList = new ArrayList<>();
        for (MonsterInfo monsterInfo : monsterInfoList) {
            Tower.MonsterInfo.Builder monster = Tower.MonsterInfo.newBuilder();
            monster.setAppearNum(monsterInfo.getAppearNum());
            monster.setMaxBet(monsterInfo.getMaxBet());
            monster.setMonsterId(monsterInfo.getId());
            monster.setMonsterName(monsterInfo.getMonsterName());
            monster.setRates(monsterInfo.getRates());
            monster.setMultiple(monsterInfo.getMultiple());
            infoList.add(monster.build());
        }
        return infoList;
    }

    /**
     * 发送房间信息
     *
     * @param userId  玩家id
     * @param roomRes 房间信息
     */
    private void sendMsg(int userId, Tower.RoomRes.Builder roomRes) {
        Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
        msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
        msgCtn.setType(MID_ROOM_RES);
        msgCtn.setDatas(roomRes.build().toByteString());
        Channel channel = MsgBossHandler.getPlayerIdChannel(userId);
        MsgUtil.sendMsg(channel, msgCtn.build());
    }
}
