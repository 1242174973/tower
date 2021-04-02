package com.tower.handler.base;

import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.GameStatus;
import com.tower.core.constant.Mid;
import com.tower.core.game.TowerGame;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.BetLog;
import com.tower.entity.Player;
import com.tower.entity.WelfareLog;
import com.tower.enums.GameCmd;
import com.tower.enums.ResultEnum;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.msg.Tower;
import com.tower.service.BetLogService;
import com.tower.service.WelfareLogService;
import com.tower.utils.ServerUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/31 16:10
 */
@Component
@Slf4j
public class GameHandler extends AbsLogicHandler<Tower.GameReq> implements Mid, ILogicHandler<Tower.GameReq> {

    @Resource
    private TowerGame towerGame;

    @Resource
    private WelfareLogService welfareLogService;

    @Resource
    private BetLogService betLogService;

    @Override
    public int getMid() {
        return MID_GAME_REQ;
    }

    @Override
    public void handle(Tower.GameReq reqMsg, Channel ch, Long userId) {
        GameCmd gameCmd = GameCmd.parseCode(reqMsg.getCmd());
        switch (gameCmd) {
            case BET:
                bet(reqMsg.getBetInfo(), userId.intValue());
                break;
            case REPETITION_BET:
                repetitionBet(userId.intValue());
                break;
            default:
                log.error("未知请求code:{}", gameCmd.getCode());
                break;
        }
    }

    /**
     * 重复下注
     * @param userId 下注玩家id
     */
    private void repetitionBet(int userId) {
        ServerUtil.assertParam(towerGame.getGameStatus().equals(GameStatus.GAME_START), "游戏不在下注状态");
        List<BetLog> betLogs = towerGame.getBetLog(userId);
        ServerUtil.assertParam(betLogs.size() <= 0, "下注后不能重复下注");
        betLogs = towerGame.getLastBetLog(userId);
        ServerUtil.assertParam(betLogs.size() > 0, "上局没有下注");
        double sum = betLogs.stream().mapToDouble(betLog -> betLog.getBetCoin().doubleValue()).sum();
        Player player = PlayerUtils.getPlayer(userId);
        ServerUtil.assertParam(player.getMoney().doubleValue() >= sum, "下注后不能重复下注");
        for (BetLog betLog : betLogs) {
            Tower.BetInfo.Builder builder=Tower.BetInfo.newBuilder();
            builder.setMonsterId(betLog.getBetMonsterId());
            builder.setUserId(betLog.getUserId());
            builder.setCoin(betLog.getBetCoin().intValue());
            bet(builder.build(),userId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void bet(Tower.BetInfo betInfo, int userId) {
        ServerUtil.assertParam(towerGame.getGameStatus().equals(GameStatus.GAME_START), "游戏不在下注状态");
        Player player = PlayerUtils.getPlayer(userId);
        ServerUtil.assertParam(player.getMoney().doubleValue() >= betInfo.getCoin(), "玩家分数不足");
        ServerUtil.assertParam(towerGame.canBet(player.getId(), betInfo.getCoin(), betInfo.getMonsterId()), "下注该怪物达到上限");
        player.setMoney(player.getMoney().subtract(BigDecimal.valueOf(betInfo.getCoin())));
        PlayerUtils.savePlayer(player);
        BetLog betLog = new BetLog();
        betLog.setBetCoin(BigDecimal.valueOf(betInfo.getCoin()));
        betLog.setOrderId(towerGame.getAttackLog().getOrderId());
        betLog.setUserId(userId);
        betLog.setBetMonsterId(betInfo.getMonsterId());
        betLog.setStatus(ResultEnum.NOT_RESULT.getCode());
        betLog.setCreateTime(LocalDateTime.now());
        MsgBossHandler.EXECUTE_BET_LOG_SAVE.execute(() -> {
            betLogService.saveOrUpdate(betLog);
            WelfareLog welfareLog = new WelfareLog().setCreateTime(LocalDateTime.now()).setUserId(player.getId())
                    .setWelfareType(WelfareTypeEnum.GOLD.getCode()).setMode(WelfareModelEnum.BET_COIN.getCode())
                    .setWelfare(BigDecimal.valueOf(-betLog.getBetCoin().doubleValue()));
            welfareLogService.save(welfareLog);
        });
        towerGame.bet(betLog);
    }
}
