package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.WithdrawLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.WithdrawLogPageDto;
import com.tower.entity.WithdrawLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.WithdrawLogService;
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
@RequestMapping("/withdrawLog")
@Api(value = "提现审核", tags = "提现审核相关请求")
public class WithdrawLogController {

    @Resource
    private WithdrawLogService withdrawLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有提现审核", notes = "获得所有提现审核请求")
    public ResponseDto<WithdrawLogPageDto> list(@RequestBody WithdrawLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<WithdrawLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                .or(queryWrapper -> queryWrapper.like(WithdrawLog::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(WithdrawLog::getCreateTime);
        Page<WithdrawLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = withdrawLogService.page(page, lambdaQueryWrapper);
        List<WithdrawLogDto> withdrawLogDtoList = CopyUtil.copyList(page.getRecords(), WithdrawLogDto.class);
        pageDto.setList(withdrawLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<WithdrawLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加提现审核", notes = "添加提现审核请求")
    public ResponseDto<WithdrawLogDto> add(@ApiParam(value = "提现审核信息", required = true)
        @RequestBody WithdrawLogDto withdrawLogDto) {
        requireParam(withdrawLogDto);
        WithdrawLog withdrawLog = CopyUtil.copy(withdrawLogDto, WithdrawLog.class);
        withdrawLog.setCreateTime(LocalDateTime.now());
        withdrawLogService.save(withdrawLog);
        ResponseDto<WithdrawLogDto> responseDto = new ResponseDto<>();
        withdrawLogDto = CopyUtil.copy(withdrawLog, WithdrawLogDto.class);
        responseDto.setContent(withdrawLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改提现审核", notes = "修改提现审核请求")
    public ResponseDto<WithdrawLogDto> edit(@ApiParam(value = "提现审核信息", required = true)
                                       @RequestBody WithdrawLogDto withdrawLogDto) {
        requireParam(withdrawLogDto);
        BusinessUtil.require(withdrawLogDto.getId(), BusinessExceptionCode.ID);
        WithdrawLog withdrawLog = withdrawLogService.getById(withdrawLogDto.getId());
        BusinessUtil.assertParam(withdrawLog != null, "提现审核没找到");
         withdrawLogService.saveOrUpdate(withdrawLog);
        ResponseDto<WithdrawLogDto> responseDto = new ResponseDto<>();
        withdrawLogDto = CopyUtil.copy(withdrawLog,WithdrawLogDto.class);
        responseDto.setContent(withdrawLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除提现审核", notes = "删除提现审核请求")
    public ResponseDto<String> delete(@ApiParam(value = "提现审核ID", required = true)
                                      @PathVariable int id) {
        withdrawLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
    * 校验参数
    *
    * @param withdrawLogDto 参数
    */
    private void requireParam(WithdrawLogDto withdrawLogDto) {

    }
}
