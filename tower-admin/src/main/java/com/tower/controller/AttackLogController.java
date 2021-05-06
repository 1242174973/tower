package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.AttackLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.AttackLogPageDto;
import com.tower.entity.AttackLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.AttackLogService;
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
@RequestMapping("/attackLog")
@Api(value = "开奖结果记录", tags = "开奖结果记录相关请求")
public class AttackLogController {

    @Resource
    private AttackLogService attackLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有开奖结果记录", notes = "获得所有开奖结果记录请求")
    public ResponseDto<AttackLogPageDto> list(@RequestBody AttackLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<AttackLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(AttackLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(AttackLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(AttackLog::getMonsterId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(AttackLog::getVer, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(AttackLog::getCreateTime);
        Page<AttackLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = attackLogService.page(page, lambdaQueryWrapper);
        List<AttackLogDto> attackLogDtoList = CopyUtil.copyList(page.getRecords(), AttackLogDto.class);
        pageDto.setList(attackLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<AttackLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加开奖结果记录", notes = "添加开奖结果记录请求")
    public ResponseDto<AttackLogDto> add(@ApiParam(value = "开奖结果记录信息", required = true)
                                         @RequestBody AttackLogDto attackLogDto) {
        requireParam(attackLogDto);
        AttackLog attackLog = CopyUtil.copy(attackLogDto, AttackLog.class);
        attackLog.setCreateTime(LocalDateTime.now());
        attackLogService.save(attackLog);
        ResponseDto<AttackLogDto> responseDto = new ResponseDto<>();
        attackLogDto = CopyUtil.copy(attackLog, AttackLogDto.class);
        responseDto.setContent(attackLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改开奖结果记录", notes = "修改开奖结果记录请求")
    public ResponseDto<AttackLogDto> edit(@ApiParam(value = "开奖结果记录信息", required = true)
                                          @RequestBody AttackLogDto attackLogDto) {
        requireParam(attackLogDto);
        BusinessUtil.require(attackLogDto.getId(), BusinessExceptionCode.ID);
        AttackLog attackLog = attackLogService.getById(attackLogDto.getId());
        BusinessUtil.assertParam(attackLog != null, "开奖结果记录没找到");
        attackLogService.saveOrUpdate(attackLog);
        ResponseDto<AttackLogDto> responseDto = new ResponseDto<>();
        attackLogDto = CopyUtil.copy(attackLog, AttackLogDto.class);
        responseDto.setContent(attackLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除开奖结果记录", notes = "删除开奖结果记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "开奖结果记录ID", required = true)
                                      @PathVariable int id) {
        attackLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param attackLogDto 参数
     */
    private void requireParam(AttackLogDto attackLogDto) {

    }
}
