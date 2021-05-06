package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.BetLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.BetLogPageDto;
import com.tower.entity.BetLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.BetLogService;
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
@RequestMapping("/betLog")
@Api(value = "下注记录", tags = "下注记录相关请求")
public class BetLogController {

    @Resource
    private BetLogService betLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有下注记录", notes = "获得所有下注记录请求")
    public ResponseDto<BetLogPageDto> list(@RequestBody BetLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<BetLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(BetLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(BetLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(BetLog::getUserId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(BetLog::getCreateTime);
        Page<BetLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = betLogService.page(page, lambdaQueryWrapper);
        List<BetLogDto> betLogDtoList = CopyUtil.copyList(page.getRecords(), BetLogDto.class);
        pageDto.setList(betLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<BetLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加下注记录", notes = "添加下注记录请求")
    public ResponseDto<BetLogDto> add(@ApiParam(value = "下注记录信息", required = true)
                                      @RequestBody BetLogDto betLogDto) {
        requireParam(betLogDto);
        BetLog betLog = CopyUtil.copy(betLogDto, BetLog.class);
        betLog.setCreateTime(LocalDateTime.now());
        betLogService.save(betLog);
        ResponseDto<BetLogDto> responseDto = new ResponseDto<>();
        betLogDto = CopyUtil.copy(betLog, BetLogDto.class);
        responseDto.setContent(betLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改下注记录", notes = "修改下注记录请求")
    public ResponseDto<BetLogDto> edit(@ApiParam(value = "下注记录信息", required = true)
                                       @RequestBody BetLogDto betLogDto) {
        requireParam(betLogDto);
        BusinessUtil.require(betLogDto.getId(), BusinessExceptionCode.ID);
        BetLog betLog = betLogService.getById(betLogDto.getId());
        BusinessUtil.assertParam(betLog != null, "下注记录没找到");
        betLogService.saveOrUpdate(betLog);
        ResponseDto<BetLogDto> responseDto = new ResponseDto<>();
        betLogDto = CopyUtil.copy(betLog, BetLogDto.class);
        responseDto.setContent(betLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除下注记录", notes = "删除下注记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "下注记录ID", required = true)
                                      @PathVariable int id) {
        betLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param betLogDto 参数
     */
    private void requireParam(BetLogDto betLogDto) {

    }
}
