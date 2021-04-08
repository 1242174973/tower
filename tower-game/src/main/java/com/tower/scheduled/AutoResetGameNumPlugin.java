package com.tower.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.game.TowerGame;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.Player;
import com.tower.entity.ProfitLog;
import com.tower.entity.ProfitRebateLog;
import com.tower.service.PlayerService;
import com.tower.service.ProfitLogService;
import com.tower.service.ProfitRebateLogService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/25 10:45
 */
@Component
@Slf4j
public class AutoResetGameNumPlugin {

    @Resource
    private TowerGame towerGame;

    @Resource
    private ProfitLogService profitLogService;

    @Resource
    private ProfitRebateLogService profitRebateLogService;

    @Resource
    private PlayerService playerService;

    /**
     * 每天凌晨1点执行一次    重置所有提现次数   "0 0 0 * * ? ";//每天凌晨0:00:00执行一次,?用于无指定日期
     * //@Scheduled(cron = "*\/5 * * * * ?")
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetGame() {
        towerGame.setNum(0);
        towerGame.getAttackLogList().clear();
        if (DateUtils.getDate(0).equals(DateUtils.getPeriod())) {
            log.info("到达一周期,开始结算盈利返利 结算日期:{}", DateUtils.getPeriod());
            LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<Player> players = playerService.getBaseMapper().selectList(lambdaQueryWrapper);
            for (Player player : players) {
                LambdaQueryWrapper<ProfitLog> profitLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
                profitLogLambdaQueryWrapper.eq(ProfitLog::getUserId, player.getId()).ge(ProfitLog::getCreateTime, DateUtils.getLastPeriod())
                        .le(ProfitLog::getCreateTime, DateUtils.getPeriod());
                double profit = profitLogService.getBaseMapper().selectList(profitLogLambdaQueryWrapper)
                        .stream().mapToDouble(ProfitLog::getProfitCoin).sum();
                log.info("玩家:{}，结算盈利返利，返利金额为:{}", player.getId(), profit);
                player.setCanAward(player.getCanAward().add(BigDecimal.valueOf(profit)))
                        .setTotalAward(player.getTotalAward().add(BigDecimal.valueOf(profit)));
                PlayerUtils.savePlayer(player);
                ProfitRebateLog rebateLog = new ProfitRebateLog()
                        .setRebateCoin(profit)
                        .setCreateTime(LocalDateTime.now())
                        .setUserId(player.getId());
                profitRebateLogService.save(rebateLog);
            }

        }
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.getDate(0));
        System.out.println(DateUtils.getPeriod());
    }
}
