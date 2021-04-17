package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.*;
import com.tower.dto.page.game.ChallengeRewardPageDto;
import com.tower.dto.page.game.SalvagePageDto;
import com.tower.entity.ChallengeReward;
import com.tower.entity.Player;
import com.tower.entity.Salvage;
import com.tower.entity.WelfareLog;
import com.tower.enums.AwardStatus;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.service.SalvageService;
import com.tower.service.WelfareLogService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.service.my.MySalvageService;
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
@RequestMapping("/salvage")
@Api(value = "救助金", tags = "救助金相关请求")
public class SalvageController {

    @Resource
    private MySalvageService salvageService;

    @Resource
    private WelfareLogService welfareLogService;

    @GetMapping("/todaySalvage")
    @ApiOperation(value = "今日预计援助金", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<SalvageDto> todaySalvage(Player player) {
        LambdaQueryWrapper<Salvage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Salvage::getUserId, player.getId())
                .ge(Salvage::getCreateTime, DateUtils.getDate(0))
                .lt(Salvage::getCreateTime, DateUtils.getDate(1));
        Salvage salvage = salvageService.getOne(lambdaQueryWrapper);
        BusinessUtil.assertParam(salvage != null, "找不到今日援助奖励信息");
        SalvageDto salvageDto = CopyUtil.copy(salvage, SalvageDto.class);
        lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Salvage::getUserId, player.getId())
                .ge(Salvage::getCreateTime, DateUtils.getDate(0))
                .lt(Salvage::getCreateTime, DateUtils.getDate(1));
        salvage = salvageService.getOne(lambdaQueryWrapper);
        if (salvage != null) {
            salvageDto.setYesterdaySalvage(salvage.getSalvage());
        }
        val responseDto = new ResponseDto<SalvageDto>();
        responseDto.setContent(salvageDto);
        return responseDto;
    }

    @PostMapping("/salvageList")
    @ApiOperation(value = "援助金明细", notes = "参数 分页参数 日期年 日期月")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<SalvagePageDto> rewardList(Player player,
                                                          @RequestBody SalvagePageDto salvagePageDto) {
        BusinessUtil.assertParam(salvagePageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(salvagePageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<SalvagePageDto> responseDto = new ResponseDto<>();
        Page<Salvage> page = new Page<>(salvagePageDto.getPage(), salvagePageDto.getSize());
        LambdaQueryWrapper<Salvage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Salvage::getUserId, player.getId());
        String year = salvagePageDto.getYear() + "";
        String month = salvagePageDto.getMonth() < 10 ? "0" + salvagePageDto.getMonth() : salvagePageDto.getMonth() + "";
        String startTime = year + "-" + month + "-" + "01";
        String endTime;
        if (salvagePageDto.getMonth() >= 12) {
            year = salvagePageDto.getYear() + 1 + "";
            month = "01";
        }
        endTime = year + "-" + month + "-" + "01";
        lambdaQueryWrapper.ge(Salvage::getCreateTime, startTime);
        lambdaQueryWrapper.lt(Salvage::getCreateTime, endTime);
        page = salvageService.page(page, lambdaQueryWrapper);
        salvagePageDto.setTotal((int) page.getTotal());
        List<SalvageDto> salvageDtoList = CopyUtil.copyList(page.getRecords(), SalvageDto.class);
        salvagePageDto.setList(salvageDtoList);
        salvagePageDto.setTotalSalvage(salvageService.selectTotalSalvage(player.getId()));
        salvagePageDto.setTotalGet(salvageService.selectTotalGet(player.getId()));
        responseDto.setContent(salvagePageDto);
        return responseDto;
    }
    @GetMapping("/getSalvage")
    @ApiOperation(value = "领取救助金", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> getSalvage(Player player) {
        LambdaQueryWrapper<Salvage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Salvage::getUserId, player.getId())
                .eq(Salvage::getStatus, AwardStatus.CLEARING);
        List<Salvage> salvages = salvageService.getBaseMapper().selectList(lambdaQueryWrapper);
        BusinessUtil.assertParam(salvages != null && salvages.size() > 0, "暂时没有可领取奖励");
        //可领取的奖励
        double sum = salvages.stream().mapToDouble(salvage -> salvage.getSalvage().doubleValue()).sum();
        //添加福利记录
        WelfareLog welfareLog = new WelfareLog().setCreateTime(LocalDateTime.now()).setUserId(player.getId())
                .setWelfareType(WelfareTypeEnum.GOLD.getCode()).setMode(WelfareModelEnum.RESCUE.getCode())
                .setWelfare(BigDecimal.valueOf(sum));
        welfareLogService.save(welfareLog);
        //领取更新数据库
        List<Integer> rewardIds = salvages.stream().map(Salvage::getId).collect(Collectors.toList());
        salvageService.getSalvage(rewardIds);
        player.setMoney(player.getMoney().add(BigDecimal.valueOf(sum)));
        return PlayerUtils.getPlayerDtoResponseDto(player);
    }
}
