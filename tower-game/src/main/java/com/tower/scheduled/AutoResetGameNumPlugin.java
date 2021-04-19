package com.tower.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.game.TowerGame;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.*;
import com.tower.service.*;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.service.my.MySalvageService;
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
    @Resource
    private AgentRebateService agentRebateService;
    @Resource
    private BetLogService betLogService;
    @Resource
    private MyChallengeRewardService challengeRewardService;
    @Resource
    private ExtracLogService extracLogService;
    @Resource
    private GameLogService gameLogService;
    @Resource
    private SafeBoxLogService safeBoxLogService;
    @Resource
    private MySalvageService salvageService;
    @Resource
    private ShareLogService shareLogService;
    @Resource
    private TopUpLogService topUpLogService;
    @Resource
    private TransferLogService transferLogService;
    @Resource
    private WelfareLogService welfareLogService;
    @Resource
    private WithdrawLogService withdrawLogService;


    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetGameNum() {
        towerGame.setNum(0);
        towerGame.getAttackLogList().clear();
        removeLastMonthLog();
    }

    public void removeLastMonthLog(){
        String day=DateUtils.getLastMonthDay();
        agentRebateService.remove(new LambdaQueryWrapper<AgentRebate>().lt(AgentRebate::getCreateTime, day));
        betLogService.remove(new LambdaQueryWrapper<BetLog>().lt(BetLog::getCreateTime, day));
        challengeRewardService.remove(new LambdaQueryWrapper<ChallengeReward>().lt(ChallengeReward::getCreateTime, day));
        extracLogService.remove(new LambdaQueryWrapper<ExtracLog>().lt(ExtracLog::getCreateTime, day));
        gameLogService.remove(new LambdaQueryWrapper<GameLog>().lt(GameLog::getCreateTime, day));
        profitLogService.remove(new LambdaQueryWrapper<ProfitLog>().lt(ProfitLog::getCreateTime, day));
        profitRebateLogService.remove(new LambdaQueryWrapper<ProfitRebateLog>().lt(ProfitRebateLog::getCreateTime, day));
        safeBoxLogService.remove(new LambdaQueryWrapper<SafeBoxLog>().lt(SafeBoxLog::getCreateTime, day));
        salvageService.remove(new LambdaQueryWrapper<Salvage>().lt(Salvage::getCreateTime, day));
        shareLogService.remove(new LambdaQueryWrapper<ShareLog>().lt(ShareLog::getCreateTime, day));
        topUpLogService.remove(new LambdaQueryWrapper<TopUpLog>().lt(TopUpLog::getCreateTime, day));
        transferLogService.remove(new LambdaQueryWrapper<TransferLog>().lt(TransferLog::getCreateTime, day));
        welfareLogService.remove(new LambdaQueryWrapper<WelfareLog>().lt(WelfareLog::getCreateTime, day));
        withdrawLogService.remove(new LambdaQueryWrapper<WithdrawLog>().lt(WithdrawLog::getCreateTime, day));
    }
    /**
     * 每天凌晨1点执行一次    重置所有提现次数   "0 0 0 * * ? ";//每天凌晨0:00:00执行一次,?用于无指定日期
     * //@Scheduled(cron = "*\/5 * * * * ?")
     */
    @Scheduled(cron = "* */5 * * * ?")
    public void resetGame() {
        if (DateUtils.isDay(1) || DateUtils.isDay(11) || DateUtils.isDay(21) || 1 == 1) {
            log.info("到达一周期,开始结算盈利返利 结算日期:{}", DateUtils.getPeriod());
            List<Player> players = playerService.list();
            for (Player player : players) {
                LambdaQueryWrapper<ProfitLog> profitLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
                profitLogLambdaQueryWrapper
                        .eq(ProfitLog::getStatus, 0)
                        .eq(ProfitLog::getUserId, player.getId())
                        .ge(ProfitLog::getCreateTime, DateUtils.getLastPeriod())
                        .lt(ProfitLog::getCreateTime,  DateUtils.getDate(1));
                List<ProfitLog> profitLogs = profitLogService.list(profitLogLambdaQueryWrapper);
                for (ProfitLog profitLog : profitLogs) {
                    profitLog.setStatus(1);
                    profitLogService.updateById(profitLog);
                }
                double profit = profitLogs.stream().mapToDouble(ProfitLog::getProfitCoin).sum();
                log.info("玩家:{}，结算盈利返利，返利金额为:{}", player.getId(), profit);
                if (profit == 0) {
                    continue;
                }
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
        System.out.println(DateUtils.getLastPeriod());
        System.out.println(DateUtils.getPeriod());
        System.out.println(DateUtils.isDay(15));
        System.out.println(DateUtils.isDay(16));
    }
}
