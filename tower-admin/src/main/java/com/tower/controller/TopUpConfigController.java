package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.TopUpConfigDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.TopUpConfigPageDto;
import com.tower.entity.TopUpConfig;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.TopUpConfigService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author xxxx
 */
@RestController
@RequestMapping("/topUpConfig")
@Api(value = "充值信息", tags = "充值信息相关请求")
public class TopUpConfigController {

    @Resource
    private TopUpConfigService topUpConfigService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有充值信息", notes = "获得所有充值信息请求")
    public ResponseDto<TopUpConfigPageDto> list(@RequestBody TopUpConfigPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<TopUpConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(TopUpConfig::getBankCardName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpConfig::getBankCardNum, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpConfig::getSubBranch, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpConfig::getModel, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpConfig::getType, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(TopUpConfig::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(TopUpConfig::getCreateTime);
        Page<TopUpConfig> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = topUpConfigService.page(page, lambdaQueryWrapper);
        List<TopUpConfigDto> topUpConfigDtoList = CopyUtil.copyList(page.getRecords(), TopUpConfigDto.class);
        pageDto.setList(topUpConfigDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<TopUpConfigPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加充值信息", notes = "添加充值信息请求")
    public ResponseDto<TopUpConfigDto> add(@ApiParam(value = "充值信息信息", required = true)
                                           @RequestBody TopUpConfigDto topUpConfigDto) {
        requireParam(topUpConfigDto);
        TopUpConfig topUpConfig = CopyUtil.copy(topUpConfigDto, TopUpConfig.class);
        topUpConfig.setCreateTime(LocalDateTime.now());
        topUpConfigService.save(topUpConfig);
        ResponseDto<TopUpConfigDto> responseDto = new ResponseDto<>();
        topUpConfigDto = CopyUtil.copy(topUpConfig, TopUpConfigDto.class);
        responseDto.setContent(topUpConfigDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改充值信息", notes = "修改充值信息请求")
    public ResponseDto<TopUpConfigDto> edit(@ApiParam(value = "充值信息信息", required = true)
                                            @RequestBody TopUpConfigDto topUpConfigDto) {
        requireParam(topUpConfigDto);
        BusinessUtil.require(topUpConfigDto.getId(), BusinessExceptionCode.ID);
        TopUpConfig topUpConfig = topUpConfigService.getById(topUpConfigDto.getId());
        BusinessUtil.assertParam(topUpConfig != null, "充值信息没找到");
        topUpConfig.setBankCardName(topUpConfigDto.getBankCardName());
        topUpConfig.setBankCardNum(topUpConfigDto.getBankCardNum());
        topUpConfig.setModel(topUpConfigDto.getModel());
        topUpConfig.setPayee(topUpConfigDto.getPayee());
        topUpConfig.setSubBranch(topUpConfigDto.getSubBranch());
        topUpConfig.setType(topUpConfigDto.getType());
        topUpConfig.setMinTopUp(topUpConfigDto.getMinTopUp());
        topUpConfig.setMaxTopUp(topUpConfigDto.getMaxTopUp());
        topUpConfigService.saveOrUpdate(topUpConfig);
        ResponseDto<TopUpConfigDto> responseDto = new ResponseDto<>();
        topUpConfigDto = CopyUtil.copy(topUpConfig, TopUpConfigDto.class);
        responseDto.setContent(topUpConfigDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除充值信息", notes = "删除充值信息请求")
    public ResponseDto<String> delete(@ApiParam(value = "充值信息ID", required = true)
                                      @PathVariable int id) {
        topUpConfigService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param topUpConfigDto 参数
     */
    private void requireParam(TopUpConfigDto topUpConfigDto) {
        BusinessUtil.assertParam(topUpConfigDto.getBankCardName() != null, "银行卡名不能为空");
        BusinessUtil.require(topUpConfigDto.getBankCardNum(), BusinessExceptionCode.BANK_CARD_NUM);
        BusinessUtil.require(topUpConfigDto.getType(), BusinessExceptionCode.TYPE);
        BusinessUtil.require(topUpConfigDto.getModel(), BusinessExceptionCode.MODEL);
        BusinessUtil.require(topUpConfigDto.getSubBranch(), BusinessExceptionCode.SUB_BRANCH);
        BusinessUtil.require(topUpConfigDto.getPayee(), BusinessExceptionCode.PAYEE);
    }
}
