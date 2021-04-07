package com.tower.handler.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.Mid;
import com.tower.core.game.TowerGame;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.MsgUtil;
import com.tower.entity.BetLog;
import com.tower.enums.RecordCmd;
import com.tower.msg.Tower;
import com.tower.service.BetLogService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @Resource
    private BetLogService betLogService;

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
                betLog(reqMsg, userId.intValue());
                break;
            default:
                log.error("未知指令{}", recordCmd.getCode());
                break;
        }
    }

    /**
     * 粗略记录
     *
     * @param reqMsg 请求
     * @param userId 玩家id
     */
    private void betLog(Tower.RecordReq reqMsg, int userId) {
        LambdaQueryWrapper<BetLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(BetLog::getUserId, userId).orderByDesc(BetLog::getCreateTime);
        Page<BetLog> page = new Page<>(reqMsg.getPage(), reqMsg.getSize());
        page = betLogService.page(page, logLambdaQueryWrapper);
        Tower.RecordRes.Builder recordRes = Tower.RecordRes.newBuilder();
        recordRes.setCmd(RecordCmd.BET_LOG.getCode());
        Tower.BetPageLog.Builder betInfoBuilder = Tower.BetPageLog.newBuilder();
        betInfoBuilder.setPage((int) page.getPages());
        betInfoBuilder.setSize((int) page.getSize());
        betInfoBuilder.setCount((int) page.getTotal());
        List<BetLog> records = page.getRecords();
        List<Tower.BetLog> betLogList = new ArrayList<>();
        records.forEach(record -> {
            Tower.BetLog.Builder betLog = Tower.BetLog.newBuilder();
            betLog.setOneBet(record.getOneBet().intValue());
            betLog.setTwoBet(record.getTwoBet().intValue());
            betLog.setThreeBet(record.getThreeBet().intValue());
            betLog.setFourBet(record.getFourBet().intValue());
            betLog.setFiveBet(record.getFiveBet().intValue());
            betLog.setSixBet(record.getSixBet().intValue());
            betLog.setSevenBet(record.getSevenBet().intValue());
            betLog.setEightBet(record.getEightBet().intValue());
            betLog.setOrderId(record.getOrderId());
            if (record.getResultMonster() != null) {
                betLog.setResultMonster(record.getResultMonster());
                betLog.setResultCoin(record.getResultCoin().intValue());
                betLog.setResultTime(record.getResultTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
            betLog.setStatus(record.getStatus());
            betLog.setCreateTime(record.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            betLogList.add(betLog.build());
        });
        betInfoBuilder.addAllBetLog(betLogList);
        recordRes.setBetPageLog(betInfoBuilder);
        sendMsg(recordRes, userId);
    }

    /**
     * 详细记录
     *
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
