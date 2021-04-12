package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.EditionEtcDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.EditionEtc;
import com.tower.service.EditionEtcService;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/12 13:51
 */
@RestController
@RequestMapping("/editionEtc")
@Api(value = "版本信息", tags = "版本信息相关请求")
public class EditionEtcController {
    @Resource
    private EditionEtcService editionEtcService;

    @GetMapping("/getEditionEtcDto/{platform}")
    @ApiOperation(value = "获得版本信息", notes = "参数 平台")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<EditionEtcDto> getEditionEtcDto(@ApiParam("平台")
                                                       @PathVariable int platform) {
        LambdaQueryWrapper<EditionEtc> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EditionEtc::getPlatform, platform);
        EditionEtc one = editionEtcService.getOne(lambdaQueryWrapper);
        EditionEtcDto copy = CopyUtil.copy(one, EditionEtcDto.class);
        ResponseDto<EditionEtcDto> responseDto = new ResponseDto<>();
        responseDto.setContent(copy);
        return responseDto;
    }

}
