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
import com.tower.game.MonsterInfo;
import com.tower.msg.Tower;
import com.tower.service.*;
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
    private BetLogService betLogService;

    @Resource
    private AgentRebateService agentRebateService;

    @Resource
    private ProfitLogService profitLogService;

    @Resource
    private ChallengeRewardService challengeRewardService;

    @Resource
    private SalvageService salvageService;

    @Resource
    private GameLogService gameLogService;

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
        betLogList = new ArrayList<>();
        gameStatus = GameStatus.NULL;
        executeHashedWheelTimer = new ExecuteHashedWheelTimer();
        LambdaQueryWrapper<AttackLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.like(AttackLog::getOrderId, DateUtils.getYearAndMonthAndDay()).orderByDesc(AttackLog::getCreateTime);
        Page<AttackLog> page = new Page<>(1, 1);
        page = attackLogService.page(page, logLambdaQueryWrapper);
        attackLogList = attackLogService.getBaseMapper().selectList(logLambdaQueryWrapper);
//        if (page.getRecords().size() <= 0) {
//            setNum(0);
//        } else {
//            AttackLog attackLog = page.getRecords().get(0);
//            String replace = attackLog.getOrderId().replace(DateUtils.getYearAndMonthAndDay(), "");
//            setNum(Integer.parseInt(replace));
//        }
        setNum((int) page.getTotal());
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
        log.info("7秒后出怪");
        int countdown = 7000;
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
        settleBetInfo();
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
            MsgBossHandler.EXECUTE_BET_LOG_SAVE.execute(() -> {
                Map<Integer, Double> playerWinCoinMap = new HashMap<>(16);
                Map<Integer, Double> playerBetWinCoinMap = new HashMap<>(64);
                Map<Integer, Double> playerRebateCoinMap = new HashMap<>(64);
                for (BetLog betLog : currBetList) {
                    updateBetLog(now, one, betLog);
                    double totalBet = betLog.getOneBet().doubleValue()
                            + betLog.getTwoBet().doubleValue()
                            + betLog.getThreeBet().doubleValue()
                            + betLog.getFourBet().doubleValue()
                            + betLog.getFiveBet().doubleValue()
                            + betLog.getSixBet().doubleValue()
                            + betLog.getSevenBet().doubleValue()
                            + betLog.getEightBet().doubleValue();
                    playerBetWinCoinMap.merge(betLog.getUserId(), -totalBet, Double::sum);
                    playerBetWinCoinMap.merge(betLog.getUserId(), betLog.getResultCoin().doubleValue(), Double::sum);
                    List<Player> playerList = new ArrayList<>();
                    Player player = PlayerUtils.getPlayer(betLog.getUserId());
                    getAllSuper(player, playerList);
                    saveChallengeReward(betLog, totalBet);
                    saveSalvage(betLog, totalBet);
                    double rebateCoin = rebate(betLog, playerList, totalBet);
                    double winCoin = totalBet - betLog.getResultCoin().doubleValue();
                    taxRemoveCoin(playerList, winCoin, playerWinCoinMap, betLog);
                    taxRemoveCoin(playerList, rebateCoin, playerRebateCoinMap, betLog);
                }
                playerWinCoinMap.forEach((key, value) -> {
                    ProfitLog profitLog = new ProfitLog()
                            .setProfitCoin(value)
                            .setUserId(key)
                            .setOrderId(currBetList.get(0).getOrderId())
                            .setCreateTime(LocalDateTime.now());
                    profitLogService.save(profitLog);
                    Player player = PlayerUtils.getPlayer(key);
                    player.setExpectedAward(player.getExpectedAward().add(BigDecimal.valueOf(value)));
                    PlayerUtils.savePlayer(player);
                });
                playerRebateCoinMap.forEach((key, value) -> {
                    Player player = PlayerUtils.getPlayer(key);
                    player.setRebateAward(player.getRebateAward().subtract(BigDecimal.valueOf(value)));
                    PlayerUtils.savePlayer(player);
                });
                playerBetWinCoinMap.forEach((key, value) -> {
                    GameLog gameLog = new GameLog()
                            .setCreateTime(LocalDateTime.now())
                            .setName("酷狗英雄塔防").setRoomId(1)
                            .setOrderId(currBetList.get(0).getOrderId())
                            .setUserId(key).setProfit(value);
                    gameLogService.save(gameLog);
                });
            });
        }
    }

    /**
     * 在盈利中删除
     *
     * @param betLog     下注信息
     * @param playerList 玩家信息
     */
    private void removeRebate(BetLog betLog, List<Player> playerList) {
        double totalBet = betLog.getOneBet().doubleValue()
                + betLog.getTwoBet().doubleValue()
                + betLog.getThreeBet().doubleValue()
                + betLog.getFourBet().doubleValue()
                + betLog.getFiveBet().doubleValue()
                + betLog.getSixBet().doubleValue()
                + betLog.getSevenBet().doubleValue()
                + betLog.getEightBet().doubleValue();
        Player player = getAgentPlayer(betLog, playerList);
        if (player == null) {
            return;
        }
        double rebateCoin = totalBet * player.getRebate().doubleValue() / 100;
        rebateCoin = -rebateCoin;
        AgentRebate agentRebate = new AgentRebate()
                .setRebate(BigDecimal.valueOf(rebateCoin))
                .setAgentUserId(player.getId())
                .setUserId(betLog.getUserId())
                .setChallenge(BigDecimal.valueOf(totalBet))
                .setStatus(2)
                .setCreateTime(LocalDateTime.now());
//        player.setTotalAward(player.getTotalAward().add(BigDecimal.valueOf(rebateCoin)));
        player.setCanAward(player.getCanAward().add(BigDecimal.valueOf(rebateCoin)));
        PlayerUtils.savePlayer(player);
        agentRebateService.save(agentRebate);
    }

    public Player getAgentPlayer(BetLog betLog, List<Player> playerList) {
        List<Player> collect = playerList.stream()
                .filter(player -> player.getId().equals(betLog.getUserId()))
                .collect(Collectors.toList());
        if (collect.size() <= 0) {
            return null;
        }
        Player player = collect.get(0);
        while (true) {
            if (player == null) {
                return null;
            }
            if (player.getSuperId().equals(1)) {
                break;
            }
            player = getSuper(player, playerList);
        }
        return player;
    }

    /**
     * 保存救助金记录
     *
     * @param betLog 下注信息
     */
    private void saveSalvage(BetLog betLog, double totalBet) {
        LambdaQueryWrapper<Salvage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Salvage::getUserId, betLog.getUserId())
                .ge(Salvage::getCreateTime, DateUtils.getDate(0))
                .lt(Salvage::getCreateTime, DateUtils.getDate(1));
        Salvage salvage = salvageService.getOne(queryWrapper);
        Player player = PlayerUtils.getPlayer(betLog.getUserId());
        if (player == null || salvage == null) {
            return;
        }
        salvage.setProfit(salvage.getProfit() + betLog.getResultCoin().doubleValue() - totalBet);
        salvageService.saveOrUpdate(salvage);
    }

    /**
     * 保存挑战奖励
     *
     * @param betLog 下注信息
     */
    private void saveChallengeReward(BetLog betLog, double totalBet) {
        LambdaQueryWrapper<ChallengeReward> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChallengeReward::getUserId, betLog.getUserId())
                .ge(ChallengeReward::getCreateTime, DateUtils.getDate(0))
                .lt(ChallengeReward::getCreateTime, DateUtils.getDate(1));
        ChallengeReward challengeReward = challengeRewardService.getOne(queryWrapper);
        Player player = PlayerUtils.getPlayer(betLog.getUserId());
        if (player == null || challengeReward == null) {
            return;
        }
        challengeReward.setChallenge(challengeReward.getChallenge().add(BigDecimal.valueOf(totalBet)));
        challengeRewardService.saveOrUpdate(challengeReward);
    }

    /**
     * 进行代理输赢记录保存
     *
     * @param playerWinCoinMap 玩家盈利信息
     * @param playerList       玩家列表
     * @param winCoin          输赢分
     */
    private void tax(List<Player> playerList, double winCoin, Map<Integer, Double> playerWinCoinMap) {
        for (Player p : playerList) {
            Player lower = getLower(p, playerList);
            double tax;
            if (lower == null) {
                tax = p.getTax().doubleValue();
            } else {
                tax = p.getTax().doubleValue() - lower.getTax().doubleValue();
            }
            if (tax <= 0) {
                continue;
            }
            double taxCoin = winCoin * tax / 100;
            playerWinCoinMap.merge(p.getId(), taxCoin, Double::sum);
        }
    }

    /**
     * 进行代理输赢记录保存
     *
     * @param playerWinCoinMap 玩家盈利信息
     * @param playerList       玩家列表
     * @param winCoin          输赢分
     */
    private void taxRemoveCoin(List<Player> playerList, double winCoin, Map<Integer, Double> playerWinCoinMap, BetLog betLog) {
        Player player = getAgentPlayer(betLog, playerList);
        if (player == null) {
            return;
        }
        playerWinCoinMap.merge(player.getId(), winCoin, Double::sum);
    }

    /**
     * 返利给玩家
     *
     * @param betLog 下注记录
     * @return 返利分数
     */
    private double rebate(BetLog betLog, List<Player> playerList, double totalBet) {
        double resultCoin = 0;
        for (Player p : playerList) {
            Player lower = getLower(p, playerList);
            double rebate;
            if (lower == null) {
                rebate = p.getRebate().doubleValue();
            } else {
                rebate = p.getRebate().doubleValue() - lower.getRebate().doubleValue();
            }
            if (rebate <= 0) {
                continue;
            }
            double rebateCoin = totalBet * rebate / 100;
            resultCoin += rebateCoin;
            AgentRebate agentRebate = new AgentRebate()
                    .setRebate(BigDecimal.valueOf(rebateCoin))
                    .setAgentUserId(p.getId())
                    .setUserId(betLog.getUserId())
                    .setChallenge(BigDecimal.valueOf(totalBet))
                    .setStatus(2)
                    .setCreateTime(LocalDateTime.now());
//            p.setTotalAward(p.getTotalAward().add(BigDecimal.valueOf(rebateCoin)));
            p.setCanAward(p.getCanAward().add(BigDecimal.valueOf(rebateCoin)));
            PlayerUtils.savePlayer(p);
            agentRebateService.save(agentRebate);
        }
        return resultCoin;
    }

    /**
     * 获得list中的下级
     *
     * @param p          玩家
     * @param playerList 所有玩家列表
     * @return 下级
     */
    private Player getLower(Player p, List<Player> playerList) {
        for (Player player : playerList) {
            if (player.getSuperId().equals(p.getId())) {
                return player;
            }
        }
        return null;
    }

    /**
     * 获得list中的下级
     *
     * @param p          玩家
     * @param playerList 所有玩家列表
     * @return 下级
     */
    private Player getSuper(Player p, List<Player> playerList) {
        for (Player player : playerList) {
            if (player.getId().equals(p.getSuperId())) {
                return player;
            }
        }
        return null;
    }

    /**
     * 获得所有上级
     *
     * @param player 玩家
     */
    private void getAllSuper(Player player, List<Player> playerList) {
        if (player == null) {
            return;
        }
        if (!player.getSuperId().equals(0)) {
            getAllSuper(PlayerUtils.getPlayer(player.getSuperId()), playerList);
        }
        playerList.add(player);
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
        betLog.setResultMonster(one.getMonsterId());
        double betCoin = getBetCoin(betLog, one);
        if (betCoin > 0) {
            int multiple = getMonsterInfoById(one.getMonsterId()).getMultiple();
            betLog.setResultCoin(BigDecimal.valueOf(betCoin).multiply(BigDecimal.valueOf(multiple)));
            Player playerInfo = PlayerUtils.getPlayer(betLog.getUserId());
            playerInfo.setMoney(playerInfo.getMoney().add(betLog.getResultCoin()));
            PlayerUtils.savePlayer(playerInfo);
        } else {
            betLog.setResultCoin(BigDecimal.ZERO);
        }
        betLog.setStatus(ResultEnum.ALREADY_RESULT.getCode());
        betLogService.saveOrUpdate(betLog);
    }

    private double getBetCoin(BetLog betLog, AttackLog one) {
        if (betLog == null) {
            return 0.0;
        }
        switch (one.getMonsterId()) {
            case 1:
                return betLog.getOneBet().doubleValue();
            case 2:
                return betLog.getTwoBet().doubleValue();
            case 3:
                return betLog.getThreeBet().doubleValue();
            case 4:
                return betLog.getFourBet().doubleValue();
            case 5:
                return betLog.getFiveBet().doubleValue();
            case 6:
                return betLog.getSixBet().doubleValue();
            case 7:
                return betLog.getSevenBet().doubleValue();
            case 8:
                return betLog.getEightBet().doubleValue();
            default:
                return 0.0;
        }
    }

    /**
     * 发送游戏结束
     *
     * @param countdown 倒计时
     */
    public void sendGameOver(int countdown) {
        attackLogList.add(0, this.attackLog);
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
            double sum = getBetCoin(getBetLog(roomUserId), this.attackLog);
            MonsterInfo monsterInfo = getMonsterInfoById(monsterId);
            gameOverInfo.setWinCoin((int) (sum * monsterInfo.getMultiple()));

            gameRes.setGameOverInfo(gameOverInfo);

            Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
            msgCtn.setDatas(gameRes.build().toByteString());
            msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
            msgCtn.setType(Mid.MID_GAME_RES);

            Channel channel = MsgBossHandler.getPlayerIdChannel(roomUserId);
            MsgUtil.sendMsg(channel, msgCtn.build());
        });
    }

    public List<AttackLog> getPageAttackLog(int page, int size) {
        page -= 1;
        int start = page * size;
        int end = (page + 1) * size;
        return attackLogList.stream().filter(attackLog -> {
            String replace = attackLog.getOrderId().replace(DateUtils.getYearAndMonthAndDay(), "");
            try {
                int num = Integer.parseInt(replace);
                return num >= start && num < end;
            } catch (Exception e) {
                return false;
            }
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
        int size = Math.min(attackLogList.size(), 10);
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
            attackLog.setMonsterId(this.attackLog.getMonsterId() == null ? 0 : this.attackLog.getMonsterId());
            gameOverInfo.setAttackLog(attackLog);
            gameOverInfo.addAllRecommendId(getRecommendIds());
            gameOverInfo.addAllRecommendMonster(getRecommendMonster());
            gameRes.setGameOverInfo(gameOverInfo);
            sendToId(gameRes, userId);
        }
    }

    /**
     * 玩家下注
     */
    public void bet(Tower.BetInfo betInfo, int userId) {
        Tower.BetInfo.Builder builder = Tower.BetInfo.newBuilder();
        builder.setCoin(betInfo.getCoin());
        builder.setMonsterId(betInfo.getMonsterId());
        builder.setUserId(userId);
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.BET.getCode());
        gameRes.setBetInfo(builder.build());
        sendToId(gameRes, userId);
    }

    /**
     * 判断能否下注
     *
     * @param userId 玩家id
     * @param coin   下注分数
     * @return 是否可以下注
     */
    public boolean canBet(int userId, int coin, int monsterId) {
        BetLog betLog = getBetLog(userId);
        if (betLog == null) {
            return true;
        }
        MonsterInfo monsterInfo = getMonsterInfoById(monsterId);
        switch (monsterInfo.getId()) {
            case 1:
                return betLog.getOneBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 2:
                return betLog.getTwoBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 3:
                return betLog.getThreeBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 4:
                return betLog.getFourBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 5:
                return betLog.getFiveBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 6:
                return betLog.getSixBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 7:
                return betLog.getSevenBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
            case 8:
                return betLog.getEightBet().doubleValue() + coin <= monsterInfo.getMaxBet().doubleValue();
        }
        return false;
    }

    /**
     * 获得本局下注
     *
     * @param userId 玩家id
     * @return 返回数据
     */
    public BetLog getBetLog(int userId) {
        List<BetLog> bets = betLogList.stream()
                .filter(betLog -> betLog.getUserId().equals(userId))
                .collect(Collectors.toList());
        return bets.size() > 0 ? bets.get(0) : null;
    }

    public BetLog getLastBetLog(int userId) {
        LambdaQueryWrapper<BetLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        String orderId = DateUtils.getYearAndMonthAndDay() + getLastIndex();
        logLambdaQueryWrapper.eq(BetLog::getUserId, userId).eq(BetLog::getOrderId, orderId);
        return betLogService.getOne(logLambdaQueryWrapper);
    }

    /**
     * 发送下注信息
     *
     * @param userId 玩家id
     */
    public void sendBetInfos(int userId) {
        BetLog betLog = getBetLog(userId);
        if (betLog == null) {
            return;
        }
        sendBetCoin(betLog.getOneBet().intValue(), userId, 1);
        sendBetCoin(betLog.getTwoBet().intValue(), userId, 2);
        sendBetCoin(betLog.getThreeBet().intValue(), userId, 3);
        sendBetCoin(betLog.getFourBet().intValue(), userId, 4);
        sendBetCoin(betLog.getFiveBet().intValue(), userId, 5);
        sendBetCoin(betLog.getSixBet().intValue(), userId, 6);
        sendBetCoin(betLog.getSevenBet().intValue(), userId, 7);
        sendBetCoin(betLog.getEightBet().intValue(), userId, 8);
    }

    private void sendBetCoin(int betCoin, int userId, int monsterId) {
        if (betCoin <= 0) {
            return;
        }
        Tower.GameRes.Builder gameRes = Tower.GameRes.newBuilder();
        gameRes.setCmd(GameCmd.BET_INFO.getCode());
        List<Tower.BetInfo> betList = new ArrayList<>();
        Tower.BetInfo.Builder builder = Tower.BetInfo.newBuilder();
        builder.setCoin(betCoin);
        builder.setUserId(userId);
        builder.setMonsterId(monsterId);
        betList.add(builder.build());
        gameRes.addAllBetInfos(betList);
        sendToId(gameRes, userId);
    }

    public synchronized BetLog getBetLogOrNew(int userId) {
        List<BetLog> bets = betLogList.stream()
                .filter(betLog -> betLog.getUserId().equals(userId))
                .collect(Collectors.toList());
        if (bets.size() > 0) {
            return bets.get(0);
        }
        BetLog betLog = new BetLog();
        betLog.setUserId(userId)
                .setOrderId(getAttackLog().getOrderId())
                .setCreateTime(LocalDateTime.now())
                .setOneBet(BigDecimal.ZERO)
                .setTwoBet(BigDecimal.ZERO)
                .setThreeBet(BigDecimal.ZERO)
                .setFourBet(BigDecimal.ZERO)
                .setFiveBet(BigDecimal.ZERO)
                .setSixBet(BigDecimal.ZERO)
                .setSevenBet(BigDecimal.ZERO)
                .setEightBet(BigDecimal.ZERO)
                .setStatus(ResultEnum.NOT_RESULT.getCode());
        betLogList.add(betLog);
        return betLog;
    }

    public List<Integer> getBetMonsterIds(int userId) {
        BetLog betLog = getBetLog(userId);
        List<Integer> list = new ArrayList<>();
        if (betLog == null) {
            return list;
        }
        if (betLog.getOneBet().doubleValue() > 0) {
            list.add(1);
        }
        if (betLog.getTwoBet().doubleValue() > 0) {
            list.add(2);
        }
        if (betLog.getThreeBet().doubleValue() > 0) {
            list.add(3);
        }
        if (betLog.getFourBet().doubleValue() > 0) {
            list.add(4);
        }
        if (betLog.getFiveBet().doubleValue() > 0) {
            list.add(5);
        }
        if (betLog.getSixBet().doubleValue() > 0) {
            list.add(6);
        }
        if (betLog.getSevenBet().doubleValue() > 0) {
            list.add(7);
        }
        if (betLog.getEightBet().doubleValue() > 0) {
            list.add(8);
        }
        return list;
    }
}
