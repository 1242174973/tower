package com.tower.core.game;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.constant.GameStatus;
import com.tower.core.constant.Mid;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.thread.ExecuteHashedWheelTimer;
import com.tower.core.utils.MsgUtil;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.*;
import com.tower.enums.GameCmd;
import com.tower.enums.ResultEnum;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.game.MonsterInfo;
import com.tower.msg.Tower;
import com.tower.service.AttackLogService;
import com.tower.service.BetLogService;
import com.tower.service.MonsterService;
import com.tower.service.WelfareLogService;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Resource
    private WelfareLogService welfareLogService;

    @Resource
    private BetLogService betLogService;

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
     * 下注集合
     */
    private List<BetLog> betLogList;
    /**
     * 游戏状态
     */
    private GameStatus gameStatus;

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
        gameStatus = GameStatus.NULL;
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
        gameStatus = GameStatus.GAME_START;
        betLogList = new ArrayList<>();
        log.info("30秒后即将出怪");
        int countdown = 30000;
        currStartTime = System.currentTimeMillis();
        executeHashedWheelTimer.newTimeout(this::upcomingAward, countdown, TimeUnit.MILLISECONDS);
        attackLog = new AttackLog().setCreateTime(LocalDateTime.now()).setVer(getVer()).setOrderId(DateUtils.getYearAndMonthAndDay() + getIndex());
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

    private String getLastIndex() {
        StringBuilder stringBuilder = new StringBuilder();
        int currNum = num - 1;
        int length = 4 - String.valueOf(currNum).length();
        for (int i = 0; i < length; i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(currNum);
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
        gameStatus = GameStatus.UPCOMING_AWARD;
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
        gameStatus = GameStatus.AWARD;
        if (monsterId == null) {
            log.error("无法获取此局出怪信息，延迟4秒，等待开奖数据获取 ver:{}", getVer());
            executeHashedWheelTimer.newTimeout(this::awardDelay, 4, TimeUnit.SECONDS);
            return;
        }
        int countdown = 14000;
        sendGameOver(countdown);
        settleBetInfo();
    }

    private void settleBetInfo() {
        List<BetLog> currBetList = new ArrayList<>(betLogList);
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<AttackLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (currBetList.size() <= 0) {
            return;
        }
        logLambdaQueryWrapper.eq(AttackLog::getOrderId, currBetList.get(0).getOrderId());
        AttackLog one = attackLogService.getOne(logLambdaQueryWrapper);
        if (one != null && one.getMonsterId() != null) {
            for (BetLog betLog : currBetList) {
                MsgBossHandler.EXECUTE_BET_LOG_SAVE.execute(() ->
                        updateBetLog(now, one, betLog)
                );
            }
        }
    }

    /**
     * 计算开奖结果
     *
     * @param now    开奖时间
     * @param one    开奖期数据
     * @param betLog 下注记录
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateBetLog(LocalDateTime now, AttackLog one, BetLog betLog) {
        betLog.setResultTime(now);
        betLog.setResultMonsterId(one.getMonsterId());
        if (betLog.getBetMonsterId().equals(one.getMonsterId())) {
            int multiple = getMonsterInfoById(one.getMonsterId()).getMultiple();
            betLog.setResultCoin(betLog.getBetCoin().multiply(BigDecimal.valueOf(multiple)));
            Player playerInfo = PlayerUtils.getPlayer(betLog.getUserId());
            playerInfo.setMoney(playerInfo.getMoney().add(betLog.getResultCoin()));
            PlayerUtils.savePlayer(playerInfo);
            WelfareLog welfareLog = new WelfareLog().setCreateTime(LocalDateTime.now()).setUserId(playerInfo.getId())
                    .setWelfareType(WelfareTypeEnum.GOLD.getCode()).setMode(WelfareModelEnum.BET_WIN.getCode())
                    .setWelfare(betLog.getResultCoin());
            welfareLogService.save(welfareLog);
        } else {
            betLog.setResultCoin(BigDecimal.ZERO);
        }
        betLog.setStatus(ResultEnum.ALREADY_RESULT.getCode());
        betLogService.saveOrUpdate(betLog);
    }

    /**
     * 发送游戏结束
     *
     * @param countdown 倒计时
     */
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

        Set<Integer> roomUserIds = MsgBossHandler.getRoomUserIds();
        roomUserIds.forEach(roomUserId -> {

            double sum = betLogList.stream().filter(betLog -> betLog.getUserId().equals(roomUserId) && betLog.getBetMonsterId().equals(monsterId))
                    .mapToDouble(betLog -> betLog.getBetCoin().doubleValue())
                    .sum();
            MonsterInfo monsterInfo = getMonsterInfoById(roomUserId);
            gameOverInfo.setWinCoin((int) (sum * monsterInfo.getMultiple()));

            gameRes.setGameOverInfo(gameOverInfo);

            Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
            msgCtn.setDatas(gameRes.build().toByteString());
            msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
            msgCtn.setType(Mid.MID_GAME_RES);

            Channel channel = MsgBossHandler.getPlayerIdChannel(roomUserId);
            MsgUtil.sendMsg(channel, msgCtn.build());
        });
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
        int size = 10;
        List<AttackLog> attackLogs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
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
        int maxTime = 50000;
        int startTime = 30000;
        int startAndAttackTime = 36000;
        if (currStartTime <= 0) {
            return;
        }
        long time = System.currentTimeMillis() - currStartTime;
        if (time < 0 || time > maxTime) {
            time = 0;
        }
        if (time < startTime) {
            //开始时间
            Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.GAME_START.getCode()).setCountdown((int) (startTime - time));
            sendToId(gameRes, userId);
        } else if (time < startAndAttackTime) {
            //怪物即将到来
            Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.UPCOMING_AWARD.getCode()).setCountdown((int) (startAndAttackTime - time));
            sendToId(gameRes, userId);
        } else {
            //怪物已经进攻
            Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.AWARD.getCode()).setCountdown((int) (maxTime - time));
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

    /**
     * 玩家下注
     *
     * @param betLog 下注记录
     */
    public void bet(BetLog betLog) {
        betLogList.add(betLog);
        Tower.BetInfo.Builder builder = Tower.BetInfo.newBuilder();
        builder.setCoin(betLog.getBetCoin().intValue());
        builder.setMonsterId(betLog.getBetMonsterId());
        builder.setUserId(betLog.getUserId());
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.BET.getCode());
        gameRes.setBetInfo(builder.build());
        sendToAll(gameRes);
    }

    /**
     * 判断能否下注
     *
     * @param userId 玩家id
     * @param coin   下注分数
     * @return 是否可以下注
     */
    public boolean canBet(int userId, int coin, int monsterId) {
        double betCoin = betLogList.stream()
                .filter(betLog -> betLog.getUserId().equals(userId) && betLog.getBetMonsterId().equals(monsterId))
                .mapToDouble(betLog -> betLog.getBetCoin().doubleValue())
                .sum();
        MonsterInfo monsterInfo = getMonsterInfoById(monsterId);
        return betCoin + coin <= monsterInfo.getMaxBet().doubleValue();
    }

    /**
     * 获得本局下注
     *
     * @param userId 玩家id
     * @return 返回数据
     */
    public List<BetLog> getBetLog(int userId) {
        return betLogList.stream()
                .filter(betLog -> betLog.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<BetLog> getLastBetLog(int userId) {
        LambdaQueryWrapper<BetLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        String orderId = DateUtils.getYearAndMonthAndDay() + getLastIndex();
        logLambdaQueryWrapper.eq(BetLog::getUserId, userId).eq(BetLog::getOrderId, orderId);
        return betLogService.getBaseMapper().selectList(logLambdaQueryWrapper);
    }

    /**
     * 发送下注信息
     *
     * @param userId 玩家id
     */
    public void sendBetInfos(int userId) {
        List<BetLog> betLogs = getBetLog(userId);
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.BET_INFO.getCode());
        List<Tower.BetInfo> betList = new ArrayList<>();
        for (BetLog betLog : betLogs) {
            Tower.BetInfo.Builder builder = Tower.BetInfo.newBuilder();
            builder.setCoin(betLog.getBetCoin().intValue());
            builder.setUserId(betLog.getUserId());
            builder.setMonsterId(betLog.getBetMonsterId());
            betList.add(builder.build());
        }
        gameRes.addAllBetInfos(betList);
        sendToId(gameRes, userId);
    }
}
