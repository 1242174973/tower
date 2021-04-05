package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.*;
import com.tower.dto.page.game.AgentTeamPageDto;
import com.tower.dto.page.game.ExtracLogPageDto;
import com.tower.entity.*;
import com.tower.exception.BusinessException;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.*;
import com.tower.service.my.MyAgentRebateService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private ChallengeRewardService challengeRewardService;

    @Resource
    private SalvageService salvageService;

    @Resource
    private ExtracLogService extracLogService;

    @Resource
    private ShareLogService shareLogService;

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
                .ge(Player::getCreateTime, DateUtils.getDate(-1))
                .le(Player::getCreateTime, DateUtils.getDate(0));
        agentDto.setNewNum(playerService.count(playerLambdaQueryWrapper));
        double money = agentRebateService.selectExpectedReward(player.getId(), DateUtils.getPeriod());
        agentDto.setExpectedReward(BigDecimal.valueOf(money));
        ResponseDto<AgentDto> responseDto = new ResponseDto<>();
        responseDto.setContent(agentDto);
        return responseDto;
    }

    @GetMapping("/addAgent/{account}/{password}/{rebate}")
    @ApiOperation(value = "添加会员", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<String> addAgent(Player player,
                                        @ApiParam(value = "账号", required = true)
                                        @PathVariable String account,
                                        @ApiParam(value = "密码", required = true)
                                        @PathVariable String password,
                                        @ApiParam(value = "密码", required = true)
                                        @PathVariable double rebate) {
        BusinessUtil.require(account, BusinessExceptionCode.ACCOUNT);
        BusinessUtil.length(account, BusinessExceptionCode.ACCOUNT, 6, 20);
        BusinessUtil.require(password, BusinessExceptionCode.PASSWORD);
        BusinessUtil.length(password, BusinessExceptionCode.PASSWORD, 6, 20);
        BusinessUtil.assertParam(player.getRebate().doubleValue() > rebate, "不能设置比自己更高的返利");
        LambdaQueryWrapper<Player> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.or(wrapper -> wrapper.eq(Player::getAccount, account));
        Player sqlPlayer = playerService.getOne(queryWrapper);
        BusinessUtil.assertParam(sqlPlayer == null, "用户已存在");
        sqlPlayer = new Player();
        sqlPlayer.setCreateTime(LocalDateTime.now());
        sqlPlayer.setMoney(BigDecimal.ZERO);
        sqlPlayer.setSafeBox(BigDecimal.ZERO);
        sqlPlayer.setSpread(UuidUtil.getShortUuid(4));
        sqlPlayer.setSuperId(player.getId());
        sqlPlayer.setPic("https://img02.sogoucdn.com/v2/thumb/retype_exclude_gif/ext/auto/q/80/crop/xy/ai/w/160/h/160/resize/w/160?url=https%3A%2F%2Fimg02.sogoucdn.com%2Fapp%2Fa%2F10010016%2F4e2cfdceac8118da34011cb5c49da00b&appid=201003&sign=676de451cea1a4192b7eede671eae0ce");
        sqlPlayer.setSalt(UuidUtil.getShortUuid(8));
        sqlPlayer.setSignInTime(DateUtils.byDayLocalDateTime(-1));
        sqlPlayer.setPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(password + player.getSalt())));
        sqlPlayer.setAccount(account);
        sqlPlayer.setNickName(NameRandomUtil.getRandomName());
        sqlPlayer.setRebate(BigDecimal.valueOf(rebate));
        sqlPlayer.setTax(BigDecimal.ZERO);
        sqlPlayer.setTotalAward(BigDecimal.ZERO);
        sqlPlayer.setCanAward(BigDecimal.ZERO);
        playerService.save(sqlPlayer);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("添加成功");
        return responseDto;
    }

    @GetMapping("/rebateLog")
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
        switch (agentTeamPageDto.getPeriod()) {
            case 1:
                //今日
                startTime = DateUtils.getDate(-1);
                stopTime = DateUtils.getDate(0);
                break;
            case 2:
                //昨日
                startTime = DateUtils.getDate(-2);
                stopTime = DateUtils.getDate(-1);
                break;
            case 20:
                //上周期
                startTime = DateUtils.getLastPeriod();
                stopTime = DateUtils.getPeriod();
                break;
            case 10:
            default:
                startTime = DateUtils.getPeriod();
                stopTime = DateUtils.getDate(0);
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

    @GetMapping("/extractRewardLog")
    @ApiOperation(value = "获得提取奖励记录", notes = "参数 分页参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BusinessException.class)
    public ResponseDto<ExtracLogPageDto> extractRewardLog(Player player, ExtracLogPageDto extracLogPageDto) {
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
}
