package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.ResponseDto;
import com.tower.dto.TopUpConfigDto;
import com.tower.entity.TopUpConfig;
import com.tower.service.TopUpConfigService;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/topUp")
@Api(value = "充值信息", tags = "充值信息相关请求")
public class TopUpController {

    @Resource
    private TopUpConfigService topUpConfigService;

    @GetMapping("/topUpList")
    @ApiOperation(value = "获取所有充值信息", notes = "无需参数")
    public ResponseDto<List<TopUpConfigDto>> monsterList() {
        ResponseDto<List<TopUpConfigDto>> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<TopUpConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<TopUpConfig> topUpConfigs = topUpConfigService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<TopUpConfigDto> topUpConfigDtoList = CopyUtil.copyList(topUpConfigs, TopUpConfigDto.class);
        responseDto.setContent(topUpConfigDtoList);
        return responseDto;
    }
}

