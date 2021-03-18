package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.MonsterDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.SignInDto;
import com.tower.entity.Monster;
import com.tower.entity.SignIn;
import com.tower.service.MonsterService;
import com.tower.service.SignInService;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @GetMapping("/signInList/{vipLevel}")
    @ApiOperation(value = "获取所有签到奖励", notes = "参数 vip等级")
    public ResponseDto<List<SignInDto>> monsterList(
            @ApiParam(value = "vip等级", required = true)
            @PathVariable int vipLevel) {
        ResponseDto<List<SignInDto>> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<SignIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SignIn::getVipLevel, vipLevel);
        List<SignIn> monsters = signInService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<SignInDto> monsterDtoList = CopyUtil.copyList(monsters, SignInDto.class);
        responseDto.setContent(monsterDtoList);
        return responseDto;
    }
}

