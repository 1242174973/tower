package com.tower.scheduled;

import com.tower.service.PlayerService;
import com.tower.service.my.MyPlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/24 17:31
 */
@Component
@Slf4j
public class AutoResetPlayerWithdrawPlugin {


    @Resource
    private MyPlayerService playerService;
    /**
     * 每天凌晨执行一次    重置所有提现次数   "0 0 0 * * ? ";//每天凌晨0:00:00执行一次,?用于无指定日期
     * //@Scheduled(cron = "*\/5 * * * * ?")
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetPlayerWithdraw() {
        long startTime= System.currentTimeMillis();
        log.info("重置玩家提现次数和额度开始");
        playerService.resetPlayerWithdraw();
        log.info("重置玩家提现次数和额度结束 操作时间{}ms",System.currentTimeMillis()-startTime);
    }
}
