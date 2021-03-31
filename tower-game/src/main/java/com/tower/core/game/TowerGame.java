package com.tower.core.game;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.constant.Mid;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.thread.ExecuteHashedWheelTimer;
import com.tower.core.utils.MsgUtil;
import com.tower.entity.AttackLog;
import com.tower.entity.Monster;
import com.tower.enums.GameCmd;
import com.tower.game.MonsterInfo;
import com.tower.msg.Tower;
import com.tower.service.AttackLogService;
import com.tower.service.MonsterService;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
     * 正在开始的时间
     */
    private long currStartTime;
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
        int countdown = 30000;
        currStartTime = System.currentTimeMillis();
        executeHashedWheelTimer.newTimeout(this::upcomingAward, countdown, TimeUnit.MILLISECONDS);
        attackLog = new AttackLog();
        attackLog.setCreateTime(LocalDateTime.now()).setVer(getVer()).setOrderId(DateUtils.getYearAndMonthAndDay() + getIndex());
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.GAME_START.getCode()).setCountdown(countdown);
        sendToAll(gameRes);
    }

    private void sendToAll(Tower.GameRes.Builder gameRes) {
        Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
        msgCtn.setDatas(gameRes.build().toByteString());
        msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
        msgCtn.setType(Mid.MID_GAME_RES);
        Set<Integer> roomUserIds = MsgBossHandler.getRoomUserIds();
        roomUserIds.forEach(roomUserId -> {
            Channel channel = MsgBossHandler.getPlayerIdChannel(roomUserId);
            MsgUtil.sendMsg(channel, msgCtn.build());
        });
    }

    private void sendToId(Tower.GameRes.Builder gameRes, int userId) {
        Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
        msgCtn.setDatas(gameRes.build().toByteString());
        msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
        msgCtn.setType(Mid.MID_GAME_RES);
        MsgUtil.sendMsg(MsgBossHandler.getPlayerIdChannel(userId), msgCtn.build());
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
        attackLogService.saveOrUpdate(attackLog);
    }

    /**
     * 即将出怪
     */
    public void upcomingAward() {
        log.info("6秒后出怪");
        int countdown = 6000;
        executeHashedWheelTimer.newTimeout(this::award, countdown, TimeUnit.MILLISECONDS);
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.UPCOMING_AWARD.getCode()).setCountdown(countdown);
        sendToAll(gameRes);
    }

    /**
     * 延迟开奖
     */
    public void awardDelay() {
        setAward(true);
        if (monsterId == null) {
            log.error("开奖失败，放弃此次开奖，交由后台开奖");
            attackLog.setAttackTime(LocalDateTime.now());
            attackLogService.save(attackLog);
            return;
        }
        int countdown = 10000;
        sendGameOver(countdown);
    }

    /**
     * 出怪
     */
    public void award() {
        if (monsterId == null) {
            log.error("无法获取此局出怪信息，延迟4秒，等待开奖数据获取 ver:{}", getVer());
            executeHashedWheelTimer.newTimeout(this::awardDelay, 4, TimeUnit.SECONDS);
            return;
        }
        int countdown = 14000;
        sendGameOver(countdown);
    }

    public void sendGameOver(int countdown) {
        log.info("完成出怪");
        setAward(true);
        initMonsterInfoList();
        initRecommendIds();
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.AWARD.getCode()).setCountdown(countdown);
        Tower.GameOverInfo.Builder gameOverInfo = Tower.GameOverInfo.newBuilder();
        Tower.AttackLog.Builder attackLog = Tower.AttackLog.newBuilder();
        attackLog.setOrderId(this.attackLog.getOrderId());
        attackLog.setMonsterId(this.attackLog.getMonsterId());
        gameOverInfo.setAttackLog(attackLog);
        gameOverInfo.addAllRecommendId(getRecommendIds());
        gameOverInfo.addAllRecommendMonster(getRecommendMonster());
        gameRes.setGameOverInfo(gameOverInfo);
        sendToAll(gameRes);
        attackLogList.add(0, this.attackLog);
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


    /**
     * 获得推荐信息
     *
     * @return 返回给客户端的记录信息
     */
    public List<Tower.RecommendMonster> getRecommendMonster() {
        List<MonsterInfo> monsterInfoList = getMonsterInfoList();
        List<Tower.RecommendMonster> infoList = new ArrayList<>();
        for (MonsterInfo monsterInfo : monsterInfoList) {
            Tower.RecommendMonster.Builder recommendMonster = Tower.RecommendMonster.newBuilder();
            recommendMonster.setMonsterId(monsterInfo.getId());
            recommendMonster.setContinuous(monsterInfo.getContinuous());
            recommendMonster.setRates(monsterInfo.getCurrRates());
            infoList.add(recommendMonster.build());
        }
        return infoList;
    }

    /**
     * 获得记录信息
     *
     * @return 返回给客户端的记录信息
     */
    public Tower.AttackPageLog getAttackLogPageList(int page, int size) {
        List<AttackLog> attackLogs = getPageAttackLog(page, size);
        List<Tower.AttackLog> logList = new ArrayList<>();
        for (AttackLog attackLog : attackLogs) {
            Tower.AttackLog.Builder log = Tower.AttackLog.newBuilder();
            log.setMonsterId(attackLog.getMonsterId());
            log.setOrderId(attackLog.getOrderId());
            logList.add(log.build());
        }
        Tower.AttackPageLog.Builder pageLog = Tower.AttackPageLog.newBuilder();
        pageLog.setPage(page);
        pageLog.setSize(size);
        pageLog.addAllAttackLog(logList);
        return pageLog.build();
    }

    /**
     * 获得记录信息
     *
     * @return 返回给客户端的记录信息
     */
    public List<Tower.DetailedAttackLog> getDetailedAttackLogList() {
        List<AttackLog> attackLogs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            attackLogs.add(attackLogList.get(i));
        }
        List<Tower.DetailedAttackLog> list = new ArrayList<>();
        attackLogs.forEach(attackLog -> {
            MonsterInfo monsterInfo = getMonsterInfoById(attackLog.getMonsterId());
            Tower.DetailedAttackLog.Builder builder = Tower.DetailedAttackLog.newBuilder();
            builder.setAttackTime(attackLog.getAttackTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            builder.setMonsterId(attackLog.getMonsterId());
            builder.setOrderId(attackLog.getOrderId());
            builder.setMonsterName(monsterInfo.getMonsterName());
            builder.setTurretName(monsterInfo.getTurretName());
            list.add(builder.build());
        });
        return list;
    }

    /**
     * 根据怪物id获得怪物信息
     *
     * @param monsterId 怪物id
     * @return 怪物信息
     */
    private MonsterInfo getMonsterInfoById(int monsterId) {
        for (MonsterInfo monsterInfo : monsterInfoList) {
            if (monsterInfo.getId().equals(monsterId)) {
                return monsterInfo;
            }
        }
        return new MonsterInfo();
    }

    /**
     * 发送当前状态id
     *
     * @param userId 玩家id
     */
    public void sendStatus(int userId) {
        if (currStartTime <= 0) {
            return;
        }
        long time = System.currentTimeMillis() - currStartTime;
        if (time < 30000) {
            //开始时间
            Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.GAME_START.getCode()).setCountdown((int) (30000 - time));
            sendToId(gameRes, userId);
        } else if (time < 36000) {
            //怪物即将到来
            Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.UPCOMING_AWARD.getCode()).setCountdown((int) (36000 - time));
            sendToId(gameRes, userId);
        } else {
            //怪物已经进攻
            Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.AWARD.getCode()).setCountdown((int) (50000 - time));
            Tower.GameOverInfo.Builder gameOverInfo = Tower.GameOverInfo.newBuilder();
            Tower.AttackLog.Builder attackLog = Tower.AttackLog.newBuilder();
            attackLog.setOrderId(this.attackLog.getOrderId());
            attackLog.setMonsterId(this.attackLog.getMonsterId());
            gameOverInfo.setAttackLog(attackLog);
            gameOverInfo.addAllRecommendId(getRecommendIds());
            gameOverInfo.addAllRecommendMonster(getRecommendMonster());
            gameRes.setGameOverInfo(gameOverInfo);
            sendToId(gameRes, userId);
        }
    }
}
