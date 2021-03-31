package com.tower.core.game;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.thread.ExecuteHashedWheelTimer;
import com.tower.entity.AttackLog;
import com.tower.entity.Monster;
import com.tower.game.MonsterInfo;
import com.tower.service.AttackLogService;
import com.tower.service.MonsterService;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private MonsterService monsterService;
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
     * 怪物信息
     */
    private List<MonsterInfo> monsterInfoList;
    /**
     * 记录信息
     */
    private List<AttackLog> attackLogList;
    /**
     * 推荐ID
     */
    private List<Integer> recommendIds;

    /**
     * 游戏初始化
     */
    public void init() {
        executeHashedWheelTimer = new ExecuteHashedWheelTimer();
        LambdaQueryWrapper<AttackLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.like(AttackLog::getOrderId, DateUtils.getYearAndMonthAndDay()).orderByDesc(AttackLog::getCreateTime);
        Page<AttackLog> page = new Page<>(1, 1);
        page = attackLogService.page(page, logLambdaQueryWrapper);
        attackLogList = attackLogService.getBaseMapper().selectList(logLambdaQueryWrapper);
        if (page.getRecords().size() <= 0) {
            setNum(0);
        } else {
            AttackLog attackLog = page.getRecords().get(0);
            String replace = attackLog.getOrderId().replace(DateUtils.getYearAndMonthAndDay(), "");
            setNum(Integer.parseInt(replace));
        }
        initMonsterInfoList();
        initRecommendIds();
        setAward(true);
    }

    /**
     * 初始化推荐怪物
     */
    private void initRecommendIds() {
        recommendIds = new ArrayList<>();
        List<Integer> list = new ArrayList<>(Arrays.asList(3, 4, 5, 6));
        recommendIds.add(list.remove(RandomUtils.nextInt(0, list.size())));
        list = new ArrayList<>(Arrays.asList(1, 2, 7, 8));
        list.remove(RandomUtils.nextInt(0, list.size()));
        recommendIds.addAll(list);
    }

    /**
     * 初始化今天的怪物信息
     */
    private void initMonsterInfoList() {
        monsterInfoList = new ArrayList<>();
        LambdaQueryWrapper<Monster> wrapper = new LambdaQueryWrapper<>();
        List<Monster> monsters = monsterService.getBaseMapper().selectList(wrapper);
        monsterInfoList = CopyUtil.copyList(monsters, MonsterInfo.class);
        for (MonsterInfo monster : monsterInfoList) {
            monster.setAppearNum(getAppearNum(monster.getId()));
            monster.setContinuous(getContinuous(monster.getId()));
            monster.setCurrRates(monster.getRates() * 10 - monster.getContinuous() + RandomUtils.nextInt(0, monster.getContinuous() * 2));
        }
    }

    private int getContinuous(int currMonsterId) {
        int num = 0;
        for (AttackLog attackLog : attackLogList) {
            if (attackLog.getMonsterId().equals(currMonsterId)) {
                return num;
            }
            num++;
        }
        return num;
    }

    private int getAppearNum(int currMonsterId) {
        LambdaQueryWrapper<AttackLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(AttackLog::getMonsterId, currMonsterId).like(AttackLog::getOrderId, DateUtils.getYearAndMonthAndDay());
        return attackLogService.count(logLambdaQueryWrapper);
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
        log.info("6秒后出怪");
        executeHashedWheelTimer.newTimeout(this::award, 6, TimeUnit.SECONDS);
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

    public List<AttackLog> getPageAttackLog(int page, int size) {
        page -= 1;
        int start = page * size;
        int end = (page + 1) * size;
        return attackLogList.stream().filter(attackLog -> {
            String replace = attackLog.getOrderId().replace(DateUtils.getYearAndMonthAndDay(), "");
            int num = Integer.parseInt(replace);
            return num >= start && num < end;
        }).collect(Collectors.toList());
    }
}
