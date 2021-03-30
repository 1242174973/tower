package com.tower.core.game;

import com.tower.core.thread.ExecuteHashedWheelTimer;
import com.tower.entity.AttackLog;
import com.tower.service.AttackLogService;
import com.tower.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/29 17:23
 */
@Component
@Data
@Slf4j
public class TowerGame {

    @Resource
    private AttackLogService attackLogService;

    /**
     * 此局版本号
     */
    private String ver;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 进攻时间
     */
    private long awardTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 是否已经进攻
     */
    private boolean award;

    /**
     * 怪物进攻记录
     */
    private AttackLog attackLog;

    /**
     * 定时器组件
     */
    private ExecuteHashedWheelTimer executeHashedWheelTimer;

    /**
     * 怪物id
     */
    private Integer monsterId;
    /**
     * 开始局数
     */
    private int num;

    /**
     * 游戏初始化
     */
    public void init() {
        setAward(true);
        executeHashedWheelTimer = new ExecuteHashedWheelTimer();
    }

    /**
     * 发送开始游戏
     */
    public void gameStart() {
        log.info("30秒后即将出怪");
        executeHashedWheelTimer.newTimeout(this::upcomingAward, 30, TimeUnit.SECONDS);
        attackLog = new AttackLog();
        attackLog.setCreateTime(LocalDateTime.now()).setVer(getVer()).setOrderId(DateUtils.getYearAndMonthAndDay() + getIndex());
    }

    private String getIndex() {
        StringBuilder stringBuilder = new StringBuilder();
        int currNum = ++num;
        int length = 4 - String.valueOf(currNum).length();
        for (int i = 0; i < length; i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(num);
        return stringBuilder.toString();
    }

    public void insertAttackLog() {
        attackLog.setAttackTime(LocalDateTime.now()).setMonsterId(getMonsterId());
        attackLogService.save(attackLog);
    }

    /**
     * 即将出怪
     */
    public void upcomingAward() {
        log.info("5秒后出怪");
        executeHashedWheelTimer.newTimeout(this::award, 5, TimeUnit.SECONDS);
    }

    /**
     * 出怪
     */
    public void award() {
        if (monsterId == null) {
            log.error("无法获取此局出怪信息，游戏停止，等待开奖数据获取 ver:{}", getVer());
            executeHashedWheelTimer.newTimeout(this::award, 5, TimeUnit.SECONDS);
            return;
        }
        log.info("完成出怪");
        setAward(true);
    }
}
