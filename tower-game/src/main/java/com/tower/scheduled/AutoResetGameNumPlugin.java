package com.tower.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.game.TowerGame;
import com.tower.entity.Player;
import com.tower.service.PlayerService;
import com.tower.service.my.MyChallengeRewardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/25 10:45
 */
@Component
public class AutoResetGameNumPlugin {

    @Resource
    private TowerGame towerGame;

    /**
     * 每天凌晨1点执行一次    重置所有提现次数   "0 0 0 * * ? ";//每天凌晨0:00:00执行一次,?用于无指定日期
     * //@Scheduled(cron = "*\/5 * * * * ?")
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetGame() {
        towerGame.setNum(0);
    }
}
