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
import com.tower.enums.GameCmd;
import com.tower.enums.ResultEnum;
import com.tower.msg.Tower;
import com.tower.service.BetLogService;
import com.tower.utils.ServerUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private void repetitionBet(int userId) {

    }

    private void bet(Tower.BetInfo betInfo, int userId) {
        Player player = PlayerUtils.getPlayer(userId);
        ServerUtil.assertParam(towerGame.getGameStatus().equals(GameStatus.GAME_START), "游戏不在下注状态");
        ServerUtil.assertParam(player.getMoney().doubleValue() > betInfo.getCoin(), "玩家分数不足");
        BetLog betLog = new BetLog();
        betLog.setBetCoin(BigDecimal.valueOf(betInfo.getCoin()));
        betLog.setOrderId(towerGame.getAttackLog().getOrderId());
        betLog.setUserId(userId);
        betLog.setBetMonsterId(betInfo.getMonsterId());
        betLog.setStatus(ResultEnum.NOT_RESULT.getCode());
        betLog.setCreateTime(LocalDateTime.now());
        towerGame.bet(betLog);
        MsgBossHandler.EXECUTE_BET_LOG_SAVE.execute(() -> {
            betLogService.saveOrUpdate(betLog);
        });
    }
}
