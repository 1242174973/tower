package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.*;
import com.tower.dto.page.game.AgentTeamPageDto;
import com.tower.dto.page.game.ExtracLogPageDto;
import com.tower.dto.page.game.PromoteDetailsPageDto;
import com.tower.entity.*;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.exception.BusinessException;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.*;
import com.tower.service.my.MyAgentRebateService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.service.my.MySalvageService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/2 11:35
 */
@RestController
@RequestMapping("/agent")
@Api(value = "代理相关请求", tags = "代理相关请求")
public class AgentController {

    @Resource
    private MyAgentRebateService agentRebateService;

    @Resource
    private PlayerService playerService;


    @Resource
    private ExtracLogService extracLogService;

    @Resource
    private ShareLogService shareLogService;

    @Resource
    private ProfitLogService profitLogService;

    @Resource
    private UserWithdrawConfigService userWithdrawConfigService;

    @Resource
    private MyChallengeRewardService challengeRewardService;

    @Resource
    private MySalvageService salvageService;

    @Resource
    private ProfitRebateLogService profitRebateLogService;

    @Resource
    private WelfareLogService welfareLogService;

    @Resource
    private TopUpLogService topUpLogService;

    @GetMapping("/agentIndex")
    @ApiOperation(value = "代理首页", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<AgentDto> agentIndex(Player player) {
        AgentDto agentDto = new AgentDto();
        agentDto.setCanAward(player.getCanAward());
        agentDto.setTotalAward(player.getTotalAward());
        agentDto.setTax(player.getTax());
        agentDto.setRebate(player.getRebate());
        LambdaQueryWrapper<Player> playerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        playerLambdaQueryWrapper.eq(Player::getSuperId, player.getId())
                .ge(Player::getCreateTime, DateUtils.getDate(0))
                .le(Player::getCreateTime, DateUtils.getDate(1));
        agentDto.setNewNum(playerService.count(playerLambdaQueryWrapper));
        double share = profitLogService.selectUserProfitByDay(player.getId(), DateUtils.getPeriod(), DateUtils.getDate(1));
        agentDto.setExpectedReward(BigDecimal.valueOf(share));
        ResponseDto<AgentDto> responseDto = new ResponseDto<>();
        responseDto.setContent(agentDto);
        return responseDto;
    }

    @GetMapping("/addAgent/{account}/{password}/{rebate}")
    @ApiOperation(value = "添加会员", notes = "参数 账号 密码 比例")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<String> addAgent(Player player,
                                        @ApiParam(value = "账号", required = true)
                                        @PathVariable String account,
                                        @ApiParam(value = "密码", required = true)
                                        @PathVariable String password,
                                        @ApiParam(value = "比例", required = true)
                                        @PathVariable double rebate) {
        rebate /= 100;
        BusinessUtil.require(account, BusinessExceptionCode.ACCOUNT);
        BusinessUtil.length(account, BusinessExceptionCode.ACCOUNT, 6, 20);
        BusinessUtil.require(password, BusinessExceptionCode.PASSWORD);
        BusinessUtil.length(password, BusinessExceptionCode.PASSWORD, 6, 20);
        BusinessUtil.assertParam(player.getRebate().doubleValue() >= rebate, "不能设置比自己更高的返利");
        LambdaQueryWrapper<Player> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.or(wrapper -> wrapper.eq(Player::getAccount, account));
        Player sqlPlayer = playerService.getOne(queryWrapper);
        BusinessUtil.assertParam(sqlPlayer == null, "用户已存在");
        sqlPlayer = new Player();
        sqlPlayer.setCreateTime(LocalDateTime.now());
        sqlPlayer.setMoney(BigDecimal.ZERO);
        sqlPlayer.setSafeBox(BigDecimal.ZERO);
        sqlPlayer.setSpread(PlayerUtils.getShortUuid());
        sqlPlayer.setSuperId(player.getId());
        sqlPlayer.setPic("https://img02.sogoucdn.com/v2/thumb/retype_exclude_gif/ext/auto/q/80/crop/xy/ai/w/160/h/160/resize/w/160?url=https%3A%2F%2Fimg02.sogoucdn.com%2Fapp%2Fa%2F10010016%2F4e2cfdceac8118da34011cb5c49da00b&appid=201003&sign=676de451cea1a4192b7eede671eae0ce");
        sqlPlayer.setSalt(UuidUtil.getShortUuid(8));
        sqlPlayer.setSignInTime(DateUtils.byDayLocalDateTime(-1));
        sqlPlayer.setPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(password + sqlPlayer.getSalt())));
        sqlPlayer.setAccount(account);
        sqlPlayer.setVip(0);
        sqlPlayer.setExperience(0);
        sqlPlayer.setNickName(NameRandomUtil.getRandomName());
        sqlPlayer.setRebate(BigDecimal.valueOf(rebate));
        sqlPlayer.setTax(BigDecimal.ZERO);
        sqlPlayer.setIsAgent(1);
        sqlPlayer.setTotalAward(BigDecimal.ZERO);
        sqlPlayer.setCanAward(BigDecimal.ZERO);
        playerService.save(sqlPlayer);
        UserWithdrawConfig userWithdrawConfig = new UserWithdrawConfig();
        userWithdrawConfig.setUserId(sqlPlayer.getId()).
                setCreateTime(LocalDateTime.now()).
                setTotalWithdrawMoney(300000.00).
                setTodayWithdrawMoney(300000.00).
                setTotalWithdrawSize(6).
                setTodayWithdrawSize(6);
        userWithdrawConfigService.saveOrUpdate(userWithdrawConfig);
        challengeRewardService.insertToday(sqlPlayer.getId());
        salvageService.insertToday(sqlPlayer.getId());
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("添加成功");
        return responseDto;
    }

    @PostMapping("/rebateLog")
    @ApiOperation(value = "返利记录和代理报表通用查询接口", notes = "参数 分页信息 周期")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<AgentTeamPageDto> rebateLog(Player player,
                                                   @ApiParam(value = "分页信息", required = true)
                                                   @RequestBody AgentTeamPageDto agentTeamPageDto) {
        BusinessUtil.assertParam(agentTeamPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(agentTeamPageDto.getSize() > 0, "条数必须大于0");
        BusinessUtil.assertParam(agentTeamPageDto.getPeriod() > 0, "周期不能为空");
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getSuperId, player.getId());
        Page<Player> page = new Page<>(agentTeamPageDto.getPage(), agentTeamPageDto.getSize());
        page = playerService.page(page, lambdaQueryWrapper);
        String startTime;
        String stopTime;
        agentTeamPageDto.setTotal((int) page.getTotal());
        switch (agentTeamPageDto.getPeriod()) {
            case 1:
                //今日
                startTime = DateUtils.getDate(0);
                stopTime = DateUtils.getDate(1);
                break;
            case 2:
                //昨日
                startTime = DateUtils.getDate(-1);
                stopTime = DateUtils.getDate(0);
                break;
            case 20:
                //上周期
                startTime = DateUtils.getLastPeriod();
                stopTime = DateUtils.getPeriod();
                break;
            case 10:
            default:
                startTime = DateUtils.getPeriod();
                stopTime = DateUtils.getDate(1);
                //本周期
                break;
        }
        List<Player> records = page.getRecords();
        List<AgentTeamDto> agentTeamDtoList = new ArrayList<>();
        records.forEach(playerInfo -> {
            List<Player> playerList = new ArrayList<>();
            getAllLower(playerInfo.getId(), playerList);
            playerList.add(playerInfo);
            AgentTeamDto agentTeamDto = new AgentTeamDto();
            agentTeamDto.setTeamId(playerInfo.getId());
            agentTeamDto.setNickName(playerInfo.getNickName());
            agentTeamDto.setRebate(playerInfo.getRebate());
            agentTeamDto.setTeamNum(playerList.size());
            int[] ints = playerList.stream().mapToInt(Player::getId).toArray();
            List<Integer> ids = Arrays.stream(ints).boxed().collect(Collectors.toList());
            LambdaQueryWrapper<ChallengeReward> challengeRewardLambdaQueryWrapper = new LambdaQueryWrapper<>();
            challengeRewardLambdaQueryWrapper.in(ChallengeReward::getUserId, ids).ge(ChallengeReward::getCreateTime, startTime).le(ChallengeReward::getCreateTime, stopTime);
            double sum = challengeRewardService.getBaseMapper().selectList(challengeRewardLambdaQueryWrapper).stream()
                    .mapToDouble(challengeReward -> challengeReward.getChallenge().doubleValue())
                    .sum();
            LambdaQueryWrapper<Salvage> salvageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            salvageLambdaQueryWrapper.in(Salvage::getUserId, ids)
                    .ge(Salvage::getCreateTime, startTime)
                    .le(Salvage::getCreateTime, stopTime);
            double profit = salvageService.getBaseMapper()
                    .selectList(salvageLambdaQueryWrapper)
                    .stream()
                    .mapToDouble(Salvage::getProfit)
                    .sum();
            LambdaQueryWrapper<AgentRebate> agentRebateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            agentRebateLambdaQueryWrapper.eq(AgentRebate::getAgentUserId, player.getId())
                    .in(AgentRebate::getUserId, ids)
                    .ge(AgentRebate::getCreateTime, startTime)
                    .le(AgentRebate::getCreateTime, stopTime);
            double agentRebateMoney = agentRebateService.getBaseMapper()
                    .selectList(agentRebateLambdaQueryWrapper)
                    .stream()
                    .mapToDouble(agentRebate -> agentRebate.getRebate().doubleValue())
                    .sum();
            agentTeamDto.setWater(BigDecimal.valueOf(sum));
            agentTeamDto.setProfit(BigDecimal.valueOf(profit));
            agentTeamDto.setRebateMoney(BigDecimal.valueOf(agentRebateMoney));
            agentTeamDtoList.add(agentTeamDto);
        });
        ResponseDto<AgentTeamPageDto> responseDto = new ResponseDto<>();
        agentTeamPageDto.setList(agentTeamDtoList);
        responseDto.setContent(agentTeamPageDto);
        return responseDto;
    }

    /**
     * 获得所有下级
     *
     * @param userId 玩家id
     */
    private void getAllLower(int userId, List<Player> players) {
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getSuperId, userId);
        List<Player> playerList = playerService.getBaseMapper().selectList(lambdaQueryWrapper);
        players.addAll(playerList);
        playerList.forEach(player -> getAllLower(player.getId(), players));
    }

    @GetMapping("/extractReward/{money}")
    @ApiOperation(value = "提取奖励", notes = "参数 提取金额")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
    public ResponseDto<String> extractReward(Player player,
                                             @ApiParam(value = "提取金额", required = true)
                                             @PathVariable double money) {
        try {
            BusinessUtil.assertParam(money > 0, "提取金额不能小于0");
            BusinessUtil.assertParam(player.getCanAward().doubleValue() >= money, "可提取金额不足");
            player.setMoney(player.getMoney().add(BigDecimal.valueOf(money)));
            player.setCanAward(player.getCanAward().subtract(BigDecimal.valueOf(money)));
            ExtracLog extracLog = new ExtracLog()
                    .setExtracCoin(BigDecimal.valueOf(money))
                    .setExtracId(player.getId())
                    .setSuccess(1)
                    .setCreateTime(LocalDateTime.now());
            extracLogService.save(extracLog);
            PlayerUtils.savePlayer(player);
            WelfareLog welfareLog = new WelfareLog()
                    .setMode(WelfareModelEnum.REBATE_EXTRACT.getCode())
                    .setWelfare(BigDecimal.valueOf(money))
                    .setWelfareType(WelfareTypeEnum.GOLD.getCode())
                    .setUserId(player.getId())
                    .setCreateTime(LocalDateTime.now());
            welfareLogService.save(welfareLog);
            ResponseDto<String> responseDto = new ResponseDto<>();
            responseDto.setContent("提取成功");
            return responseDto;
        } catch (BusinessException e) {
            ExtracLog extracLog = new ExtracLog()
                    .setExtracCoin(BigDecimal.valueOf(money))
                    .setExtracId(player.getId())
                    .setSuccess(0)
                    .setCreateTime(LocalDateTime.now());
            extracLogService.save(extracLog);
            throw e;
        }
    }

    @PostMapping("/extractRewardLog")
    @ApiOperation(value = "获得提取奖励记录", notes = "参数 分页参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BusinessException.class)
    public ResponseDto<ExtracLogPageDto> extractRewardLog(Player player, @RequestBody ExtracLogPageDto extracLogPageDto) {
        BusinessUtil.assertParam(extracLogPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(extracLogPageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<ExtracLogPageDto> responseDto = new ResponseDto<>();
        Page<ExtracLog> page = new Page<>(extracLogPageDto.getPage(), extracLogPageDto.getSize());
        LambdaQueryWrapper<ExtracLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(ExtracLog::getExtracId, player.getId()).orderByDesc(ExtracLog::getCreateTime);
        page = extracLogService.page(page, logLambdaQueryWrapper);
        extracLogPageDto.setTotal((int) page.getTotal());
        List<ExtracLogDto> extracLogDtoList = CopyUtil.copyList(page.getRecords(), ExtracLogDto.class);
        extracLogPageDto.setList(extracLogDtoList);
        responseDto.setMessage("查询成功");
        responseDto.setContent(extracLogPageDto);
        return responseDto;
    }

    @GetMapping("/share/{yieldId}/{safeBoxPassword}/{money}")
    @ApiOperation(value = "分享返利", notes = "参数 收益人id 保险柜密码 金额")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<String> share(Player player,
                                     @ApiParam(value = "受益人id", required = true)
                                     @PathVariable int yieldId,
                                     @ApiParam(value = "保险柜密码", required = true)
                                     @PathVariable String safeBoxPassword,
                                     @ApiParam(value = "金额", required = true)
                                     @PathVariable double money) {
        BusinessUtil.assertParam(yieldId > 0, "受益人id不能小于等于0");
        BusinessUtil.assertParam(safeBoxPassword != null, "保险柜密码不能为空");
        BusinessUtil.assertParam(money > 0, "分享金额不能小于等于0");
        Player yieldPlayer = PlayerUtils.getPlayer(yieldId);
        BusinessUtil.assertParam(yieldPlayer != null, "收益玩家未找到");
        BusinessUtil.assertParam(yieldPlayer.getSuperId().equals(player.getId()), "该收益玩家不是玩家的直系下级");
        BusinessUtil.assertParam(yieldPlayer.getIsAgent().equals(1), "分享的玩家必须是代理");
        BusinessUtil.assertParam(player.getCanAward().doubleValue() >= money, "分享金额不能大于可提取金额");
        BusinessUtil.assertParam(
                MD5Utils.getMD5Str(MD5Utils.getMD5Str(safeBoxPassword + player.getSalt())).equals(player.getSafeBoxPassword()),
                "保险柜密码错误");
        ShareLog shareLog = new ShareLog()
                .setShareId(player.getId())
                .setMoney(BigDecimal.valueOf(money))
                .setYieldId(yieldId)
                .setCreateTime(LocalDateTime.now());
        player.setCanAward(player.getCanAward().subtract(BigDecimal.valueOf(money)));
        PlayerUtils.savePlayer(player);
        shareLogService.save(shareLog);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setMessage("分享成功");
        return responseDto;
    }

    @PostMapping("/promoteDetails")
    @ApiOperation(value = "推广明细", notes = "参数 分页参数 每页10条")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BusinessException.class)
    public ResponseDto<PromoteDetailsPageDto> promoteDetails(Player player, @RequestBody PromoteDetailsPageDto promoteDetailsPageDto) {
        BusinessUtil.assertParam(promoteDetailsPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(promoteDetailsPageDto.getSize() > 0, "条数必须大于0");

        List<PromoteDetailsDto> promoteDetailsDtoList = new ArrayList<>();
        int day = -((promoteDetailsPageDto.getPage() - 1) * promoteDetailsPageDto.getSize());
        for (int i = 0; i < promoteDetailsPageDto.getSize(); i++) {
            String startTime = DateUtils.getDate(-i - day);
            String stopTime = DateUtils.getDate(-i - day + 1);
            PromoteDetailsDto promoteDetailsDto = new PromoteDetailsDto();
            promoteDetailsDto.setCreateTime(startTime);
            double rebate = agentRebateService.selectUserRewardByDay(player.getId(), startTime, stopTime);
            promoteDetailsDto.setRebate(rebate);
            double share = shareLogService.selectUserShareByDay(player.getId(), startTime, stopTime);
            promoteDetailsDto.setShareLower(-share);
            share = shareLogService.selectUserYieldByDay(player.getId(), startTime, stopTime);
            promoteDetailsDto.setSuperShare(share);
//            share = profitLogService.selectUserProfitByDay(player.getId(), startTime, stopTime);
            share = profitRebateLogService.selectUserProfitByDay(player.getId(), startTime, stopTime);
            promoteDetailsDto.setExhibitRebate(share);
            promoteDetailsDto.setTotalAward(promoteDetailsDto.getExhibitRebate()
                    + promoteDetailsDto.getRebate()
                    + promoteDetailsDto.getSuperShare()
                    + promoteDetailsDto.getShareLower());
            promoteDetailsDtoList.add(promoteDetailsDto);
        }
        promoteDetailsPageDto.setTotal(100);
        promoteDetailsPageDto.setList(promoteDetailsDtoList);
        ResponseDto<PromoteDetailsPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(promoteDetailsPageDto);
        return responseDto;
    }

    @GetMapping("/lowerDetails/{userId}/{period}")
    @ApiOperation(value = "下级详情", notes = "参数 下级玩家id 周期")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<LowerDetailsDto> lowerDetails(Player player,
                                                     @ApiParam(value = "下级玩家id", required = true)
                                                     @PathVariable int userId,
                                                     @ApiParam(value = "周期 1今日、2昨日、10本周期、20上周期", required = true)
                                                     @PathVariable int period) {
        Player lowerPlayer = PlayerUtils.getPlayer(userId);
        BusinessUtil.assertParam(lowerPlayer != null, "下级玩家未找到");
        BusinessUtil.assertParam(lowerPlayer.getSuperId().equals(player.getId()), "该玩家不是玩家的直系下级");
        LowerDetailsDto lowerDetailsDto = new LowerDetailsDto();
        lowerDetailsDto.setUserId(userId);
        lowerDetailsDto.setRebate(lowerPlayer.getRebate().doubleValue());
        List<Player> playerList = new ArrayList<>();
        getAllLower(userId, playerList);
        playerList.add(lowerPlayer);
        lowerDetailsDto.setTotalNum(playerList.size());
        lowerDetailsDto.setActiveNum(getActiveNum(playerList));
        lowerDetailsDto.setNewNum(getNewNum(playerList));
        LowerDetailsDto.LowerDetails lowerDetails = getOwnDetails(lowerPlayer, period);
        lowerDetailsDto.setMyDetails(lowerDetails);
        lowerDetails = getLowerDetails(lowerPlayer, period);
        lowerDetailsDto.setLowerDetails(lowerDetails);
        ResponseDto<LowerDetailsDto> responseDto = new ResponseDto<>();
        responseDto.setContent(lowerDetailsDto);
        responseDto.setMessage("查询成功");
        return responseDto;
    }

    @GetMapping("/rebateConfig/{userId}/{rebate}")
    @ApiOperation(value = "下级返点配置", notes = "参数 下级玩家id  返点数额")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<LowerDetailsDto> rebateConfig(Player player,
                                                     @ApiParam(value = "下级玩家id", required = true)
                                                     @PathVariable int userId,
                                                     @ApiParam(value = "返利比例", required = true)
                                                     @PathVariable double rebate) {
        double finalRebate = rebate / 100;
        BusinessUtil.assertParam(player.getRebate().doubleValue() >= finalRebate, "不能设置比自己更高的返利");
        Player lowerPlayer = PlayerUtils.getPlayer(userId);
        BusinessUtil.assertParam(lowerPlayer != null, "下级玩家未找到");
        BusinessUtil.assertParam(lowerPlayer.getSuperId().equals(player.getId()), "该玩家不是玩家的直系下级");
        BusinessUtil.assertParam(lowerPlayer.getIsAgent().equals(1), "设置的玩家必须是代理");
        double oldRebate = lowerPlayer.getRebate().doubleValue();
        lowerPlayer.setRebate(BigDecimal.valueOf(finalRebate));
        PlayerUtils.savePlayer(lowerPlayer);
        if (oldRebate > finalRebate) {
            List<Player> playerList = new ArrayList<>();
            getAllLower(userId, playerList);
            playerList.forEach(p -> {
                if (p.getRebate().doubleValue() > finalRebate) {
                    p.setRebate(BigDecimal.valueOf(finalRebate));
                    PlayerUtils.savePlayer(p);
                }
            });
        }
        ResponseDto<LowerDetailsDto> responseDto = new ResponseDto<>();
        responseDto.setMessage("设置成功");
        return responseDto;
    }

    @GetMapping("/statement/{period}")
    @ApiOperation(value = "报表", notes = "参数 周期")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<StatementDto> statement(Player player,
                                               @ApiParam(value = "周期 1今日、2昨日、10本周期、20上周期", required = true)
                                               @PathVariable int period) {
        StatementDto statementDto = new StatementDto();
        statementDto.setPeriod(period);
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getSuperId, player.getId());
        //直系下级
        List<Player> playerList = playerService.getBaseMapper().selectList(lambdaQueryWrapper);
        statementDto.setActiveNum(getActiveNum(playerList));
        statementDto.setNewNum(getNewNum(playerList));
        statementDto.setTotalNum(playerList.size());
        playerList = new ArrayList<>();
        getAllLower(player.getId(), playerList);
        statementDto.setOtherTotalNum(playerList.size() - statementDto.getTotalNum());
        statementDto.setOtherNewNum(getNewNum(playerList) - statementDto.getNewNum());
        statementDto.setOtherActiveNum(getActiveNum(playerList) - statementDto.getActiveNum());
        //赋值其他参数
        setStatementDto(statementDto, player, period, playerList);
        ResponseDto<StatementDto> responseDto = new ResponseDto<>();
        responseDto.setContent(statementDto);
        responseDto.setMessage("查询成功");
        return responseDto;
    }


    private String getStartTime(int period) {
        switch (period) {
            case 1:
                //今日
                return DateUtils.getDate(0);
            case 2:
                //昨日
                return DateUtils.getDate(-1);
            case 20:
                //上周期
                return DateUtils.getLastPeriod();
            case 10:
            default:
                //本周期
                return DateUtils.getPeriod();
        }
    }

    private String getStopTime(int period) {
        switch (period) {
            case 2:
                //昨日
                return DateUtils.getDate(0);
            case 20:
                //上周期
                return DateUtils.getPeriod();
            case 10:
            default:
                //本周期
                return DateUtils.getDate(1);
        }
    }

    private void setStatementDto(StatementDto statementDto, Player player, int period, List<Player> playerList) {
        String startTime = getStartTime(period);
        String stopTime = getStopTime(period);
        List<Integer> ids = Arrays.stream(playerList.stream().mapToInt(Player::getId).toArray())
                .boxed().collect(Collectors.toList());
        //返利数据
        LambdaQueryWrapper<AgentRebate> agentRebateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        agentRebateLambdaQueryWrapper.eq(AgentRebate::getAgentUserId, player.getId()).ge(AgentRebate::getCreateTime, startTime).le(AgentRebate::getCreateTime, stopTime);
        double rebate = agentRebateService.getBaseMapper().selectList(agentRebateLambdaQueryWrapper)
                .stream().mapToDouble(agentRebate -> agentRebate.getRebate().doubleValue()).sum();
        //流水数据
        LambdaQueryWrapper<ChallengeReward> challengeRewardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        challengeRewardLambdaQueryWrapper.eq(ChallengeReward::getUserId, player.getId()).ge(ChallengeReward::getCreateTime, startTime).le(ChallengeReward::getCreateTime, stopTime);
        double betCoin = challengeRewardService.getBaseMapper().selectList(challengeRewardLambdaQueryWrapper)
                .stream().mapToDouble(challengeReward -> challengeReward.getChallenge().doubleValue()).sum();
        //盈亏数据
        LambdaQueryWrapper<Salvage> salvageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salvageLambdaQueryWrapper.eq(Salvage::getUserId, player.getId()).ge(Salvage::getCreateTime, startTime).le(Salvage::getCreateTime, stopTime);
        double salvage = salvageService.getBaseMapper().selectList(salvageLambdaQueryWrapper)
                .stream().mapToDouble(Salvage::getProfit).sum();
        //充值数据
        LambdaQueryWrapper<TopUpLog> topUpLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        topUpLogLambdaQueryWrapper.eq(TopUpLog::getUserId, player.getId()).ge(TopUpLog::getCreateTime, startTime).le(TopUpLog::getCreateTime, stopTime);
        double topUp = topUpLogService.getBaseMapper().selectList(topUpLogLambdaQueryWrapper)
                .stream().mapToDouble(topUpLog -> topUpLog.getCoin() == null ? 0.0 : topUpLog.getCoin().doubleValue()).sum();
        //福利数据
        LambdaQueryWrapper<WelfareLog> welfareLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        welfareLogLambdaQueryWrapper.eq(WelfareLog::getUserId, player.getId()).ge(WelfareLog::getCreateTime, startTime).le(WelfareLog::getCreateTime, stopTime);
        double welfare = welfareLogService.getBaseMapper().selectList(welfareLogLambdaQueryWrapper)
                .stream().mapToDouble(welfareLog -> welfareLog.getWelfare().doubleValue()).sum();
        double lowerRebate;
        double lowerBetCoin;
        double lowerSalvage;
        double lowerTopUp;
        double lowerWelfare;
        if (ids.size() <= 0) {
            lowerRebate = 0;
            lowerBetCoin = 0;
            lowerSalvage = 0;
            lowerTopUp = 0;
            lowerWelfare = 0;
        } else {
            //返利数据
            agentRebateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            agentRebateLambdaQueryWrapper.in(AgentRebate::getAgentUserId, ids).ge(AgentRebate::getCreateTime, startTime).le(AgentRebate::getCreateTime, stopTime);
            lowerRebate = agentRebateService.getBaseMapper().selectList(agentRebateLambdaQueryWrapper)
                    .stream().mapToDouble(agentRebate -> agentRebate.getRebate().doubleValue()).sum();
            //流水数据
            challengeRewardLambdaQueryWrapper = new LambdaQueryWrapper<>();
            challengeRewardLambdaQueryWrapper.in(ChallengeReward::getUserId, ids).ge(ChallengeReward::getCreateTime, startTime).le(ChallengeReward::getCreateTime, stopTime);
            lowerBetCoin = challengeRewardService.getBaseMapper().selectList(challengeRewardLambdaQueryWrapper)
                    .stream().mapToDouble(challengeReward -> challengeReward.getChallenge().doubleValue()).sum();
            //盈利数据
            salvageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            salvageLambdaQueryWrapper.in(Salvage::getUserId, ids).ge(Salvage::getCreateTime, startTime).le(Salvage::getCreateTime, stopTime);
            lowerSalvage = salvageService.getBaseMapper().selectList(salvageLambdaQueryWrapper)
                    .stream().mapToDouble(Salvage::getProfit).sum();
            //充值数据
            topUpLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
            topUpLogLambdaQueryWrapper.in(TopUpLog::getUserId, ids).ge(TopUpLog::getCreateTime, startTime).le(TopUpLog::getCreateTime, stopTime);
            lowerTopUp = topUpLogService.getBaseMapper().selectList(topUpLogLambdaQueryWrapper)
                    .stream().mapToDouble(topUpLog -> topUpLog.getCoin() == null ? 0.0 : topUpLog.getCoin().doubleValue()).sum();
            //福利数据
            welfareLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
            welfareLogLambdaQueryWrapper.in(WelfareLog::getUserId, ids).ge(WelfareLog::getCreateTime, startTime).le(WelfareLog::getCreateTime, stopTime);
            lowerWelfare = welfareLogService.getBaseMapper().selectList(welfareLogLambdaQueryWrapper)
                    .stream().mapToDouble(welfareLog -> welfareLog.getWelfare().doubleValue()).sum();
        }
        //上周期盈亏返利
        double profitRebate = getProfitRebate(player.getId());
        //赋值
        statementDto.setMyRebate(rebate).setLowerRebate(lowerRebate)
                .setMyBetWater(betCoin).setLowerBetWater(lowerBetCoin)
                .setMyProfit(salvage).setLowerProfit(lowerSalvage)
                .setMyTopUp(topUp).setLowerTopUp(lowerTopUp)
                .setMyWelfare(welfare).setLowerWelfare(lowerWelfare)
                .setProfitRebate(profitRebate);
    }

    /**
     * 查询上周期返利
     *
     * @param id 玩家id
     * @return 上周期返利
     */
    private double getProfitRebate(int id) {
        String startTime = DateUtils.getLastPeriod();
        String stopTime = DateUtils.getDate(1);
        LambdaQueryWrapper<ProfitLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(ProfitLog::getUserId, id)
                .eq(ProfitLog::getStatus, 1)
                .ge(ProfitLog::getCreateTime, startTime)
                .le(ProfitLog::getCreateTime, stopTime);
        return profitLogService.getBaseMapper().selectList(logLambdaQueryWrapper)
                .stream().mapToDouble(ProfitLog::getProfitCoin).sum();
    }

    /**
     * 获得下级详情
     *
     * @param player 玩家id
     * @return 下级详情
     */
    private LowerDetailsDto.LowerDetails getLowerDetails(Player player, int period) {
        String startTime;
        String stopTime;
        switch (period) {
            case 1:
                //今日
                startTime = DateUtils.getDate(0);
                stopTime = DateUtils.getDate(1);
                break;
            case 2:
                //昨日
                startTime = DateUtils.getDate(-1);
                stopTime = DateUtils.getDate(0);
                break;
            case 20:
                //上周期
                startTime = DateUtils.getLastPeriod();
                stopTime = DateUtils.getPeriod();
                break;
            case 10:
            default:
                startTime = DateUtils.getPeriod();
                stopTime = DateUtils.getDate(1);
                //本周期
                break;
        }
        List<Player> playerList = new ArrayList<>();
        getAllLower(player.getId(), playerList);
        List<Integer> ids = Arrays.stream(playerList.stream().mapToInt(Player::getId).toArray())
                .boxed().collect(Collectors.toList());
        LowerDetailsDto.LowerDetails lowerDetails = new LowerDetailsDto.LowerDetails();
        LambdaQueryWrapper<AgentRebate> agentRebateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ids.size() == 0) {
            lowerDetails.setCoin(0).setRebate(0).setProfit(0).setWelfare(0).setTopUp(0);
            return lowerDetails;
        }
        agentRebateLambdaQueryWrapper.in(AgentRebate::getAgentUserId, ids)
                .ge(AgentRebate::getCreateTime, startTime)
                .le(AgentRebate::getCreateTime, stopTime);
        double rebate = agentRebateService.getBaseMapper().selectList(agentRebateLambdaQueryWrapper)
                .stream().mapToDouble(agentRebate -> agentRebate.getRebate().doubleValue()).sum();
        LambdaQueryWrapper<Salvage> salvageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salvageLambdaQueryWrapper.in(Salvage::getUserId, ids)
                .ge(Salvage::getCreateTime, startTime)
                .le(Salvage::getCreateTime, stopTime);
        double profit = salvageService.getBaseMapper().selectList(salvageLambdaQueryWrapper)
                .stream().mapToDouble(Salvage::getProfit).sum();
        LambdaQueryWrapper<WelfareLog> welfareLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        welfareLogLambdaQueryWrapper.in(WelfareLog::getUserId, ids)
                .ge(WelfareLog::getCreateTime, startTime)
                .le(WelfareLog::getCreateTime, stopTime);
        double welfare = welfareLogService.getBaseMapper().selectList(welfareLogLambdaQueryWrapper)
                .stream().mapToDouble(welfareLog -> welfareLog.getWelfare().doubleValue()).sum();
        LambdaQueryWrapper<TopUpLog> topUpLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        topUpLogLambdaQueryWrapper.in(TopUpLog::getUserId, ids)
                .ge(TopUpLog::getCreateTime, startTime)
                .le(TopUpLog::getCreateTime, stopTime);
        double topUp = topUpLogService.getBaseMapper().selectList(topUpLogLambdaQueryWrapper)
                .stream().mapToDouble(topUpLog -> topUpLog.getCoin() == null ? 0.0 : topUpLog.getCoin().doubleValue()).sum();
        double sum = playerList.stream().mapToDouble(player1 -> player.getMoney().doubleValue()).sum();
        lowerDetails.setCoin(sum).setRebate(rebate).setProfit(profit).setWelfare(welfare).setTopUp(topUp);
        return lowerDetails;
    }

    /**
     * 获得自己详情
     *
     * @param player 玩家id
     * @return 自己详情
     */
    private LowerDetailsDto.LowerDetails getOwnDetails(Player player, int period) {
        String startTime;
        String stopTime;
        switch (period) {
            case 1:
                //今日
                startTime = DateUtils.getDate(0);
                stopTime = DateUtils.getDate(1);
                break;
            case 2:
                //昨日
                startTime = DateUtils.getDate(-1);
                stopTime = DateUtils.getDate(0);
                break;
            case 20:
                //上周期
                startTime = DateUtils.getLastPeriod();
                stopTime = DateUtils.getPeriod();
                break;
            case 10:
            default:
                //本周期
                startTime = DateUtils.getPeriod();
                stopTime = DateUtils.getDate(1);
                break;
        }
        LowerDetailsDto.LowerDetails lowerDetails = new LowerDetailsDto.LowerDetails();
        LambdaQueryWrapper<AgentRebate> agentRebateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        agentRebateLambdaQueryWrapper.eq(AgentRebate::getAgentUserId, player.getId())
                .ge(AgentRebate::getCreateTime, startTime)
                .le(AgentRebate::getCreateTime, stopTime);
        double rebate = agentRebateService.getBaseMapper().selectList(agentRebateLambdaQueryWrapper)
                .stream().mapToDouble(agentRebate -> agentRebate.getRebate().doubleValue()).sum();
        LambdaQueryWrapper<Salvage> salvageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salvageLambdaQueryWrapper.eq(Salvage::getUserId, player.getId())
                .ge(Salvage::getCreateTime, startTime)
                .le(Salvage::getCreateTime, stopTime);
        double profit = salvageService.getBaseMapper().selectList(salvageLambdaQueryWrapper)
                .stream().mapToDouble(Salvage::getProfit).sum();
        LambdaQueryWrapper<WelfareLog> welfareLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        welfareLogLambdaQueryWrapper.eq(WelfareLog::getUserId, player.getId())
                .ge(WelfareLog::getCreateTime, startTime)
                .le(WelfareLog::getCreateTime, stopTime);
        double welfare = welfareLogService.getBaseMapper().selectList(welfareLogLambdaQueryWrapper)
                .stream().mapToDouble(welfareLog -> welfareLog.getWelfare().doubleValue()).sum();
        LambdaQueryWrapper<TopUpLog> topUpLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        topUpLogLambdaQueryWrapper.eq(TopUpLog::getUserId, player.getId())
                .ge(TopUpLog::getCreateTime, startTime)
                .le(TopUpLog::getCreateTime, stopTime);
        double topUp = topUpLogService.getBaseMapper().selectList(topUpLogLambdaQueryWrapper)
                .stream().mapToDouble(topUpLog -> topUpLog.getCoin() == null ? 0.0 : topUpLog.getCoin().doubleValue()).sum();
        lowerDetails.setCoin(player.getMoney().doubleValue()).setRebate(rebate).setProfit(profit).setWelfare(welfare).setTopUp(topUp);
        return lowerDetails;
    }

    /**
     * 查询活跃的玩家
     *
     * @param playerList 玩家数组
     * @return 活跃人数
     */
    private int getActiveNum(List<Player> playerList) {
        if (playerList.size() <= 0) {
            return 0;
        }
        AtomicInteger num = new AtomicInteger();
        playerList.forEach(player -> {
            String zone = "+8";
            long time = DateUtils.byDayLocalDateTime(0).toInstant(ZoneOffset.of(zone)).toEpochMilli();
            if (player.getSignInTime().toInstant(ZoneOffset.of(zone)).toEpochMilli() > time ||
                    player.getCreateTime().toInstant(ZoneOffset.of(zone)).toEpochMilli() > time) {
                num.getAndIncrement();
            }
        });
        return num.get();
    }

    /**
     * 查询新增的玩家
     *
     * @param playerList 玩家数组
     * @return 新增的玩家
     */
    private int getNewNum(List<Player> playerList) {
        if (playerList.size() <= 0) {
            return 0;
        }
        AtomicInteger num = new AtomicInteger();
        playerList.forEach(player -> {
            long time = DateUtils.byDayLocalDateTime(0).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (player.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() > time) {
                num.getAndIncrement();
            }
        });
        return num.get();
    }
}
