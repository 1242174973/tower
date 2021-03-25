package com.tower;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.entity.Player;
import com.tower.service.PlayerService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.utils.MyApplicationContextUti;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/16 14:38
 */
@SpringBootApplication
@EnableScheduling
public class TowerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TowerApplication.class, args);
        GameServer bean = context.getBean(GameServer.class);
        bean.start();
        //防止没有今日挑战的存在
        PlayerService playerService = context.getBean(PlayerService.class);
        MyChallengeRewardService challengeRewardService = context.getBean(MyChallengeRewardService.class);
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Player> players = playerService.getBaseMapper().selectList(lambdaQueryWrapper);
        for (Player player : players) {
            challengeRewardService.insertToday(player.getId());
        }
    }
}
