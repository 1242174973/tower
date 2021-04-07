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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/31 16:10
 */
@Component
@Slf4j
public class GameHandler extends AbsLogicHandler<Tower.GameReq> implements Mid, ILogicHandler<Tower.GameReq> {

    private ReadWriteLock reentrantLock = new ReentrantReadWriteLock();

    private Lock writeLock = reentrantLock.writeLock();
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
     *
     * @param userId 下注玩家id
     */
    private synchronized void repetitionBet(int userId) {
        ServerUtil.assertParam(towerGame.getGameStatus().equals(GameStatus.GAME_START), "游戏不在下注状态");
        BetLog betLog = towerGame.getBetLog(userId);
        ServerUtil.assertParam(betLog==null, "下注后不能重复下注");
        betLog = towerGame.getLastBetLog(userId);
        ServerUtil.assertParam(betLog!=null, "上局没有下注");
        double totalBet = betLog.getOneBet().doubleValue()
                + betLog.getTwoBet().doubleValue()
                + betLog.getThreeBet().doubleValue()
                + betLog.getFourBet().doubleValue()
                + betLog.getFiveBet().doubleValue()
                + betLog.getSixBet().doubleValue()
                + betLog.getSevenBet().doubleValue()
                + betLog.getEightBet().doubleValue();
        Player player = PlayerUtils.getPlayer(userId);
        ServerUtil.assertParam(player.getMoney().doubleValue() >= totalBet, "分数不足");
        betCoin(betLog.getOneBet().intValue(), userId, 1);
        betCoin(betLog.getTwoBet().intValue(), userId, 2);
        betCoin(betLog.getThreeBet().intValue(), userId, 3);
        betCoin(betLog.getFourBet().intValue(), userId, 4);
        betCoin(betLog.getFiveBet().intValue(), userId, 5);
        betCoin(betLog.getSixBet().intValue(), userId, 6);
        betCoin(betLog.getSevenBet().intValue(), userId, 7);
        betCoin(betLog.getEightBet().intValue(), userId, 8);
    }

    private void betCoin(int betCoin,int userId,int monsterId){
        if(betCoin<0){
            return;
        }
        Tower.BetInfo.Builder builder = Tower.BetInfo.newBuilder();
        builder.setMonsterId(monsterId);
        builder.setUserId(userId);
        builder.setCoin(betCoin);
        bet(builder.build(), userId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void bet(Tower.BetInfo betInfo, int userId) {
        ServerUtil.assertParam(betInfo.getMonsterId() >= 1 && betInfo.getMonsterId() <= 8, "下注选项不存在");
        writeLock.lock();
        try {
            ServerUtil.assertParam(towerGame.getGameStatus().equals(GameStatus.GAME_START), "游戏不在下注状态");
            ServerUtil.assertParam(towerGame.canBet(userId, betInfo.getCoin(), betInfo.getMonsterId()), "下注该怪物达到上限");
            Player player = PlayerUtils.getPlayer(userId);
            ServerUtil.assertParam(player.getMoney().doubleValue() >= betInfo.getCoin(), "玩家分数不足");
            player.setMoney(player.getMoney().subtract(BigDecimal.valueOf(betInfo.getCoin())));
            PlayerUtils.savePlayer(player);
        } finally {
            writeLock.unlock();
        }
        BetLog betLog = towerGame.getBetLogOrNew(userId);
        switch (betInfo.getMonsterId()) {
            case 1:
                betLog.setOneBet(betLog.getOneBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 2:
                betLog.setTwoBet(betLog.getTwoBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 3:
                betLog.setThreeBet(betLog.getThreeBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 4:
                betLog.setFourBet(betLog.getFourBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 5:
                betLog.setFiveBet(betLog.getFiveBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 6:
                betLog.setSixBet(betLog.getSixBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 7:
                betLog.setSevenBet(betLog.getSevenBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            case 8:
                betLog.setEightBet(betLog.getEightBet().add(BigDecimal.valueOf(betInfo.getCoin())));
                break;
            default:
                break;
        }
        towerGame.bet(betInfo,userId);
    }
}
