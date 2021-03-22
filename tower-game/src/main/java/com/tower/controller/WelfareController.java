package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.*;
import com.tower.dto.page.WelFareLogPageDto;
import com.tower.entity.Player;
import com.tower.entity.SignIn;
import com.tower.entity.WelfareLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.SignInService;
import com.tower.service.WelfareLogService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-17
 */
@RestController
@RequestMapping("/welfare")
@Api(value = "福利信息", tags = "福利信息相关请求")
public class WelfareController {

    @Resource
    private SignInService signInService;

    @Resource
    private WelfareLogService welfareLogService;

    @GetMapping("/signInList/{vipLevel}")
    @ApiOperation(value = "获取所有签到奖励", notes = "参数 vip等级")
    public ResponseDto<List<SignInDto>> monsterList(@ApiParam(value = "vip等级", required = true)
                                                    @PathVariable int vipLevel) {
        BusinessUtil.require(vipLevel, BusinessExceptionCode.VIP_LEVEL);
        ResponseDto<List<SignInDto>> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<SignIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SignIn::getVipLevel, vipLevel);
        List<SignIn> signIns = signInService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<SignInDto> signInDtoList = CopyUtil.copyList(signIns, SignInDto.class);
        responseDto.setContent(signInDtoList);
        return responseDto;
    }

    @GetMapping("/signIn")
    @ApiOperation(value = "签到", notes = "无需参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> signIn(Player player) {
        int vipLevel = player.getVip();
        LambdaQueryWrapper<SignIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SignIn::getVipLevel, vipLevel);
        int count = signInService.count(lambdaQueryWrapper);
        BusinessUtil.assertParam(
                DateUtils.isBeforeDay(0, player.getSignInTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()),
                "今天已经签到过了");
        if (player.getSignIn() >= count) {
            player.setSignIn(1);
        } else {
            player.setSignIn(player.getSignIn() + 1);
        }
        player.setSignInTime(LocalDateTime.now());
        lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SignIn::getVipLevel, vipLevel).eq(SignIn::getDay, player.getSignIn());
        SignIn one = signInService.getOne(lambdaQueryWrapper);
        switch (one.getAwardType()) {
            //金额
            case 1:
                player.setMoney(player.getMoney().add(one.getAward()));
                break;
            //vip经验
            case 2:
            default:
                break;
        }
        WelfareLog welfareLog = new WelfareLog();
        welfareLog.setMode(1);
        welfareLog.setWelfare(one.getAward());
        welfareLog.setWelfareType(one.getAwardType());
        welfareLog.setUserId(player.getId());
        welfareLog.setCreateTime(LocalDateTime.now());
        welfareLogService.save(welfareLog);
        return AccountController.getPlayerDtoResponseDto(player);
    }

    @PostMapping("/welfareLogList")
    @ApiOperation(value = "获取福利记录", notes = "参数 分页数据 第几天 哪些类型")
    public ResponseDto<WelFareLogPageDto> welfareLogList(Player player,
                                                         @ApiParam(value = "获取信息", required = true)
                                                         @RequestBody WelFareLogPageDto welFareLogPageDto) {
        BusinessUtil.assertParam(welFareLogPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(welFareLogPageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<WelFareLogPageDto> responseDto = new ResponseDto<>();
        Page<WelfareLog> page = new Page<>(welFareLogPageDto.getPage(), welFareLogPageDto.getSize());
        LambdaQueryWrapper<WelfareLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WelfareLog::getUserId, player.getId());
        if (welFareLogPageDto.getModel() > 0) {
            lambdaQueryWrapper.eq(WelfareLog::getMode, welFareLogPageDto.getModel());
        }
        String startTime;
        String endTime;
        //查询最近3天的数据
        if (welFareLogPageDto.getRecentDay() <= 0) {
            startTime = DateUtils.getDate(-2);
            endTime = DateUtils.getDate(1);
        }
        //查询N天前的数据
        else {
            startTime = DateUtils.getDate(-welFareLogPageDto.getRecentDay());
            endTime = DateUtils.getDate(-welFareLogPageDto.getRecentDay() + 1);
        }
        lambdaQueryWrapper.ge(WelfareLog::getCreateTime, startTime);
        lambdaQueryWrapper.le(WelfareLog::getCreateTime, endTime);
        page = welfareLogService.page(page, lambdaQueryWrapper);
        welFareLogPageDto.setTotal((int) page.getTotal());
        List<WelfareLogDto> welfareLogDtoList = CopyUtil.copyList(page.getRecords(), WelfareLogDto.class);
        welFareLogPageDto.setList(welfareLogDtoList);
        responseDto.setContent(welFareLogPageDto);
        return responseDto;
    }


}


