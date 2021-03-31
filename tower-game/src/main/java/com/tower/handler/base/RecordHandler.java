package com.tower.handler.base;

import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.Mid;
import com.tower.core.game.TowerGame;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.MsgUtil;
import com.tower.enums.RecordCmd;
import com.tower.msg.Tower;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/31 13:20
 */
@Component
@Slf4j
public class RecordHandler extends AbsLogicHandler<Tower.RecordReq> implements Mid, ILogicHandler<Tower.RecordReq> {

    @Resource
    private TowerGame towerGame;

    @Override
    public int getMid() {
        return MID_RECORD_REQ;
    }

    @Override
    public void handle(Tower.RecordReq reqMsg, Channel ch, Long userId) {
        RecordCmd recordCmd = RecordCmd.parseCode(reqMsg.getCmd());
        switch (recordCmd) {
            case ROUGH_ATTACK_LOG:
                roughAttackLog(reqMsg, userId.intValue());
                break;
            case DETAILED_ATTACK_LOG:
                detailedAttackLog(userId.intValue());
                break;
            case BET_LOG:
                break;
            default:
                log.error("未知指令{}", recordCmd.getCode());
                break;
        }
    }

    /**
     * 详细记录
     * @param userId 玩家id
     */
    private void detailedAttackLog(int userId) {
        Tower.RecordRes.Builder recordRes = Tower.RecordRes.newBuilder();
        List<Tower.DetailedAttackLog> attackLogPageList = towerGame.getDetailedAttackLogList();
        recordRes.setCmd(RecordCmd.DETAILED_ATTACK_LOG.getCode());
        recordRes.addAllDetailedAttackLog(attackLogPageList);
        sendMsg(recordRes, userId);
    }

    /**
     * 粗略记录
     *
     * @param reqMsg 请求
     * @param userId 玩家id
     */
    private void roughAttackLog(Tower.RecordReq reqMsg, int userId) {
        Tower.RecordRes.Builder recordRes = Tower.RecordRes.newBuilder();
        Tower.AttackPageLog attackLogPageList = towerGame.getAttackLogPageList(reqMsg.getPage(), reqMsg.getSize());
        recordRes.setAttackPageLog(attackLogPageList);
        recordRes.setCmd(RecordCmd.ROUGH_ATTACK_LOG.getCode());
        sendMsg(recordRes, userId);
    }

    /**
     * 发送消息
     *
     * @param recordRes 发送消息
     * @param userId    发现到id
     */
    private void sendMsg(Tower.RecordRes.Builder recordRes, int userId) {
        Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
        msgCtn.setType(MID_RECORD_RES);
        msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
        msgCtn.setDatas(recordRes.build().toByteString());
        MsgUtil.sendMsg(MsgBossHandler.getPlayerIdChannel(userId), msgCtn.build());
    }
}
