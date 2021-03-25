package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.ChallengeRewardDto;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.game.ChallengeRewardPageDto;
import com.tower.entity.ChallengeReward;
import com.tower.entity.Player;
import com.tower.entity.WelfareLog;
import com.tower.enums.AwardStatus;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.service.ChallengeRewardService;
import com.tower.service.WelfareLogService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/24 15:52
 */
@RestController
@RequestMapping("/challengeReward")
@Api(value = "挑战奖励", tags = "挑战奖励相关请求")
public class ChallengeRewardController {

    @Resource
    private MyChallengeRewardService challengeRewardService;

    @Resource
    private WelfareLogService welfareLogService;

    @GetMapping("/todayReward")
    @ApiOperation(value = "今日挑战奖励", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<ChallengeRewardDto> todayReward(Player player) {
        LambdaQueryWrapper<ChallengeReward> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChallengeReward::getUserId, player.getId())
                .ge(ChallengeReward::getCreateTime, DateUtils.getDate(0))
                .le(ChallengeReward::getCreateTime, DateUtils.getDate(1));
        ChallengeReward challengeReward = challengeRewardService.getOne(lambdaQueryWrapper);
        BusinessUtil.assertParam(challengeReward != null, "找不到今日挑战奖励");
        ChallengeRewardDto challengeRewardDto = CopyUtil.copy(challengeReward, ChallengeRewardDto.class);
        challengeRewardDto.setTotalRebate(challengeRewardService.selectTotalRebate(player.getId()));
        val responseDto = new ResponseDto<ChallengeRewardDto>();
        responseDto.setContent(challengeRewardDto);
        return responseDto;
    }

    @PostMapping("/rewardList")
    @ApiOperation(value = "挑战奖励明细", notes = "参数 分页参数 日期年 日期月")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<ChallengeRewardPageDto> rewardList(Player player,
                                                          @RequestBody ChallengeRewardPageDto challengeRewardPageDto) {
        BusinessUtil.assertParam(challengeRewardPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(challengeRewardPageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<ChallengeRewardPageDto> responseDto = new ResponseDto<>();
        Page<ChallengeReward> page = new Page<>(challengeRewardPageDto.getPage(), challengeRewardPageDto.getSize());
        LambdaQueryWrapper<ChallengeReward> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChallengeReward::getUserId, player.getId());
        String year = challengeRewardPageDto.getYear() + "";
        String month = challengeRewardPageDto.getMonth() < 10 ? "0" + challengeRewardPageDto.getMonth() : challengeRewardPageDto.getMonth() + "";
        String startTime = year + "-" + month + "-" + "01";
        String endTime;
        if (challengeRewardPageDto.getMonth() >= 12) {
            year = challengeRewardPageDto.getYear() + 1 + "";
            month = "01";
        }
        endTime = year + "-" + month + "-" + "01";
        lambdaQueryWrapper.ge(ChallengeReward::getCreateTime, startTime);
        lambdaQueryWrapper.le(ChallengeReward::getCreateTime, endTime);
        page = challengeRewardService.page(page, lambdaQueryWrapper);
        challengeRewardPageDto.setTotal((int) page.getTotal());
        List<ChallengeRewardDto> challengeRewardDtoList = CopyUtil.copyList(page.getRecords(), ChallengeRewardDto.class);
        challengeRewardPageDto.setList(challengeRewardDtoList);
        challengeRewardPageDto.setTotalRebate(challengeRewardService.selectTotalRebate(player.getId()));
        challengeRewardPageDto.setTotalGet(challengeRewardService.selectTotalGet(player.getId()));
        responseDto.setContent(challengeRewardPageDto);
        return responseDto;
    }

    @GetMapping("/getChallengeReward")
    @ApiOperation(value = "领取挑战奖励", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> getChallengeReward(Player player) {
        LambdaQueryWrapper<ChallengeReward> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChallengeReward::getUserId, player.getId())
                .eq(ChallengeReward::getStatus, AwardStatus.CLEARING);
        List<ChallengeReward> challengeRewards = challengeRewardService.getBaseMapper().selectList(lambdaQueryWrapper);
        BusinessUtil.assertParam(challengeRewards != null && challengeRewards.size() > 0, "暂时没有可领取奖励");
        //可领取的奖励
        double sum = challengeRewards.stream().mapToDouble(challengeReward -> challengeReward.getRebate().doubleValue()).sum();
        //添加福利记录
        WelfareLog welfareLog = new WelfareLog().setCreateTime(LocalDateTime.now()).setUserId(player.getId())
                .setWelfareType(WelfareTypeEnum.GOLD.getCode()).setMode(WelfareModelEnum.CHALLENGE.getCode())
                .setWelfare(BigDecimal.valueOf(sum));
        welfareLogService.save(welfareLog);
        //领取更新数据库
        List<Integer> rewardIds = challengeRewards.stream().map(ChallengeReward::getId).collect(Collectors.toList());
        challengeRewardService.getChallengeReward(rewardIds);
        player.setMoney(player.getMoney().add(BigDecimal.valueOf(sum)));
        return AccountController.getPlayerDtoResponseDto(player);
    }

}
