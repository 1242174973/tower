package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.ProfitInfoDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.*;
import com.tower.service.*;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/7 13:44
 */
@RestController
@RequestMapping("/profit")
@Api(value = "盈利请求", tags = "盈利相关请求")
public class ProfitController {

    @Resource
    private SalvageService salvageService;

    @Resource
    private ProfitLogService profitLogService;

    @Resource
    private TopUpLogService topUpLogService;

    @Resource
    private WelfareLogService welfareLogService;

    @Resource
    private ChallengeRewardService challengeRewardService;

    @Resource
    private AgentRebateService agentRebateService;

    @GetMapping("/profitInfo/{recentDay}")
    @ApiOperation(value = "盈利数据获取", notes = "参数 最近天数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<List<ProfitInfoDto>> todaySalvage(Player player,
                                                         @ApiParam(value = "最近天数 0 近三天 1今天 2昨天 ")
                                                         @PathVariable int recentDay) {
        List<ProfitInfoDto> profitInfoDtoList = getProfitInfoDtoList(player, recentDay);
        ResponseDto<List<ProfitInfoDto>> responseDto = new ResponseDto<>();
        responseDto.setContent(profitInfoDtoList);
        return responseDto;
    }

    private List<ProfitInfoDto> getProfitInfoDtoList(Player player, int recentDay) {
        List<ProfitInfoDto> list = new ArrayList<>();
        if (recentDay <= 0) {
            list.add(getProfitInfoDto(player, 1));
            list.add(getProfitInfoDto(player, 2));
            list.add(getProfitInfoDto(player, 3));
        } else {
            list.add(getProfitInfoDto(player, recentDay));
        }
        return list;
    }

    private ProfitInfoDto getProfitInfoDto(Player player, int recentDay) {
        String startTime = DateUtils.getDate(-recentDay+1);
        String endTime = DateUtils.getDate(-recentDay + 2);

        //盈利数据
        LambdaQueryWrapper<Salvage> salvageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salvageLambdaQueryWrapper.eq(Salvage::getUserId, player.getId()).ge(Salvage::getCreateTime, startTime).lt(Salvage::getCreateTime, endTime);
        double salvage = salvageService.getBaseMapper().selectList(salvageLambdaQueryWrapper)
                .stream().mapToDouble(Salvage::getProfit).sum();

        //充值数据
        LambdaQueryWrapper<TopUpLog> topUpLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        topUpLogLambdaQueryWrapper.eq(TopUpLog::getUserId, player.getId()).ge(TopUpLog::getCreateTime, startTime).lt(TopUpLog::getCreateTime, endTime);
        double topUp = topUpLogService.getBaseMapper().selectList(topUpLogLambdaQueryWrapper)
                .stream().mapToDouble(topUpLog -> topUpLog.getCoin()==null?0.0:topUpLog.getCoin().doubleValue()).sum();

        //福利数据
        LambdaQueryWrapper<WelfareLog> welfareLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        welfareLogLambdaQueryWrapper.eq(WelfareLog::getUserId, player.getId()).ge(WelfareLog::getCreateTime, startTime).lt(WelfareLog::getCreateTime, endTime);
        double welfare = welfareLogService.getBaseMapper().selectList(welfareLogLambdaQueryWrapper)
                .stream().mapToDouble(welfareLog -> welfareLog.getWelfare().doubleValue()).sum();

        //流水数据
        LambdaQueryWrapper<ChallengeReward> challengeRewardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        challengeRewardLambdaQueryWrapper.eq(ChallengeReward::getUserId, player.getId()).ge(ChallengeReward::getCreateTime, startTime).lt(ChallengeReward::getCreateTime, endTime);
        double betCoin = challengeRewardService.getBaseMapper().selectList(challengeRewardLambdaQueryWrapper)
                .stream().mapToDouble(challengeReward -> challengeReward.getChallenge().doubleValue()).sum();

        //返利数据
        LambdaQueryWrapper<AgentRebate> agentRebateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        agentRebateLambdaQueryWrapper.eq(AgentRebate::getAgentUserId, player.getId()).ge(AgentRebate::getCreateTime, startTime).lt(AgentRebate::getCreateTime, endTime);
        double rebate = agentRebateService.getBaseMapper().selectList(agentRebateLambdaQueryWrapper)
                .stream().mapToDouble(agentRebate -> agentRebate.getRebate().doubleValue()).sum();

        ProfitInfoDto profitInfoDto = new ProfitInfoDto();
        profitInfoDto.setCreateTime(startTime);
        profitInfoDto.setProfit(salvage);
        profitInfoDto.setWelfare(welfare);
        profitInfoDto.setTopUp(topUp);
        profitInfoDto.setWater(betCoin);
        profitInfoDto.setRebate(rebate);
        return profitInfoDto;
    }


}
