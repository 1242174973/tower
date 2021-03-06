package com.tower.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.entity.ChallengeReward;
import com.tower.entity.Player;
import com.tower.service.ChallengeRewardService;
import com.tower.service.PlayerService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.utils.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xxxx
 * @date 2021/3/25 10:45
 */
@Component
public class AutoResetRebatePlugin {

    @Resource
    private PlayerService playerService;

    @Resource
    private MyChallengeRewardService challengeRewardService;

    /**
     * 每天凌晨1点执行一次    重置所有提现次数   "0 0 0 * * ? ";//每天凌晨0:00:00执行一次,?用于无指定日期
     * //@Scheduled(cron = "*\/5 * * * * ?")
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetRebate() {
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Player> players = playerService.getBaseMapper().selectList(lambdaQueryWrapper);
        challengeRewardService.settlement();
        for (Player player : players) {
            challengeRewardService.insertToday(player.getId());
        }
    }
}
