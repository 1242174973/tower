package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.MonsterDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.SignInDto;
import com.tower.dto.WelfareLogDto;
import com.tower.dto.page.WelFareLogPageDto;
import com.tower.entity.Monster;
import com.tower.entity.Player;
import com.tower.entity.SignIn;
import com.tower.entity.WelfareLog;
import com.tower.service.MonsterService;
import com.tower.service.SignInService;
import com.tower.service.WelfareLogService;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
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
        ResponseDto<List<SignInDto>> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<SignIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SignIn::getVipLevel, vipLevel);
        List<SignIn> monsters = signInService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<SignInDto> monsterDtoList = CopyUtil.copyList(monsters, SignInDto.class);
        responseDto.setContent(monsterDtoList);
        return responseDto;
    }

    @PostMapping("/welfareLogList")
    @ApiOperation(value = "获取福利记录", notes = "参数 分页数据 第几天 哪些类型")
    public ResponseDto<WelFareLogPageDto> welfareLogList(Player player,
                                                         @ApiParam(value = "获取信息", required = true)
                                                         @RequestBody WelFareLogPageDto welFareLogPageDto) {
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
        WelFareLogPageDto welfareLogDtoList = CopyUtil.copy(page, WelFareLogPageDto.class);
        responseDto.setContent(welfareLogDtoList);
        return responseDto;
    }


}


