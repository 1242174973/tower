package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.UserWithdrawConfigDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.UserWithdrawConfigPageDto;
import com.tower.entity.UserWithdrawConfig;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.UserWithdrawConfigService;
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
 * @author 梦-屿-千-寻
 */
@RestController
@RequestMapping("/userWithdrawConfig")
@Api(value = "提现配置", tags = "提现配置相关请求")
public class UserWithdrawConfigController {

    @Resource
    private UserWithdrawConfigService userWithdrawConfigService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有提现配置", notes = "获得所有提现配置请求")
    public ResponseDto<UserWithdrawConfigPageDto> list(@RequestBody UserWithdrawConfigPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<UserWithdrawConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(UserWithdrawConfig::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserWithdrawConfig::getId, pageDto.getSearch()));

        }
        lambdaQueryWrapper.orderByDesc(UserWithdrawConfig::getCreateTime);
        Page<UserWithdrawConfig> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = userWithdrawConfigService.page(page, lambdaQueryWrapper);
        List<UserWithdrawConfigDto> userWithdrawConfigDtoList = CopyUtil.copyList(page.getRecords(), UserWithdrawConfigDto.class);
        pageDto.setList(userWithdrawConfigDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<UserWithdrawConfigPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加提现配置", notes = "添加提现配置请求")
    public ResponseDto<UserWithdrawConfigDto> add(@ApiParam(value = "提现配置信息", required = true)
                                                  @RequestBody UserWithdrawConfigDto userWithdrawConfigDto) {
        requireParam(userWithdrawConfigDto);
        UserWithdrawConfig userWithdrawConfig = CopyUtil.copy(userWithdrawConfigDto, UserWithdrawConfig.class);
        userWithdrawConfig.setCreateTime(LocalDateTime.now());
        userWithdrawConfigService.save(userWithdrawConfig);
        ResponseDto<UserWithdrawConfigDto> responseDto = new ResponseDto<>();
        userWithdrawConfigDto = CopyUtil.copy(userWithdrawConfig, UserWithdrawConfigDto.class);
        responseDto.setContent(userWithdrawConfigDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改提现配置", notes = "修改提现配置请求")
    public ResponseDto<UserWithdrawConfigDto> edit(@ApiParam(value = "提现配置信息", required = true)
                                                   @RequestBody UserWithdrawConfigDto userWithdrawConfigDto) {
        requireParam(userWithdrawConfigDto);
        BusinessUtil.require(userWithdrawConfigDto.getId(), BusinessExceptionCode.ID);
        UserWithdrawConfig userWithdrawConfig = userWithdrawConfigService.getById(userWithdrawConfigDto.getId());
        BusinessUtil.assertParam(userWithdrawConfig != null, "提现配置没找到");
        userWithdrawConfigService.saveOrUpdate(userWithdrawConfig);
        ResponseDto<UserWithdrawConfigDto> responseDto = new ResponseDto<>();
        userWithdrawConfigDto = CopyUtil.copy(userWithdrawConfig, UserWithdrawConfigDto.class);
        responseDto.setContent(userWithdrawConfigDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除提现配置", notes = "删除提现配置请求")
    public ResponseDto<String> delete(@ApiParam(value = "提现配置ID", required = true)
                                      @PathVariable int id) {
        userWithdrawConfigService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param userWithdrawConfigDto 参数
     */
    private void requireParam(UserWithdrawConfigDto userWithdrawConfigDto) {
        BusinessUtil.require(userWithdrawConfigDto.getTodayWithdrawMoney(),BusinessExceptionCode.TODAY_WITHDRAW_MONEY);
        BusinessUtil.require(userWithdrawConfigDto.getTotalWithdrawMoney(),BusinessExceptionCode.TOTAL_WITHDRAW_MONEY);
        BusinessUtil.require(userWithdrawConfigDto.getTodayWithdrawSize(),BusinessExceptionCode.TODAY_WITHDRAW_SIZE);
        BusinessUtil.require(userWithdrawConfigDto.getTotalWithdrawSize(),BusinessExceptionCode.TOTAL_WITHDRAW_SIZE);
        BusinessUtil.require(userWithdrawConfigDto.getServiceCharge(),BusinessExceptionCode.SERVICE_CHARGE);
    }
}
