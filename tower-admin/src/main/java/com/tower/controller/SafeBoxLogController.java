package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.SafeBoxLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.SafeBoxLogPageDto;
import com.tower.entity.SafeBoxLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.SafeBoxLogService;
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
@RequestMapping("/safeBoxLog")
@Api(value = "保险柜存取分记录", tags = "保险柜存取分记录相关请求")
public class SafeBoxLogController {

    @Resource
    private SafeBoxLogService safeBoxLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有保险柜存取分记录", notes = "获得所有保险柜存取分记录请求")
    public ResponseDto<SafeBoxLogPageDto> list(@RequestBody SafeBoxLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<SafeBoxLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                .or(queryWrapper -> queryWrapper.like(SafeBoxLog::getId, pageDto.getSearch()))
                .or(queryWrapper -> queryWrapper.like(SafeBoxLog::getUserId, pageDto.getSearch()))
                .or(queryWrapper -> queryWrapper.like(SafeBoxLog::getWithdraw, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(SafeBoxLog::getCreateTime);
        Page<SafeBoxLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = safeBoxLogService.page(page, lambdaQueryWrapper);
        List<SafeBoxLogDto> safeBoxLogDtoList = CopyUtil.copyList(page.getRecords(), SafeBoxLogDto.class);
        pageDto.setList(safeBoxLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<SafeBoxLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加保险柜存取分记录", notes = "添加保险柜存取分记录请求")
    public ResponseDto<SafeBoxLogDto> add(@ApiParam(value = "保险柜存取分记录信息", required = true)
        @RequestBody SafeBoxLogDto safeBoxLogDto) {
        requireParam(safeBoxLogDto);
        SafeBoxLog safeBoxLog = CopyUtil.copy(safeBoxLogDto, SafeBoxLog.class);
        safeBoxLog.setCreateTime(LocalDateTime.now());
        safeBoxLogService.save(safeBoxLog);
        ResponseDto<SafeBoxLogDto> responseDto = new ResponseDto<>();
        safeBoxLogDto = CopyUtil.copy(safeBoxLog, SafeBoxLogDto.class);
        responseDto.setContent(safeBoxLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改保险柜存取分记录", notes = "修改保险柜存取分记录请求")
    public ResponseDto<SafeBoxLogDto> edit(@ApiParam(value = "保险柜存取分记录信息", required = true)
                                       @RequestBody SafeBoxLogDto safeBoxLogDto) {
        requireParam(safeBoxLogDto);
        BusinessUtil.require(safeBoxLogDto.getId(), BusinessExceptionCode.ID);
        SafeBoxLog safeBoxLog = safeBoxLogService.getById(safeBoxLogDto.getId());
        BusinessUtil.assertParam(safeBoxLog != null, "保险柜存取分记录没找到");
         safeBoxLogService.saveOrUpdate(safeBoxLog);
        ResponseDto<SafeBoxLogDto> responseDto = new ResponseDto<>();
        safeBoxLogDto = CopyUtil.copy(safeBoxLog,SafeBoxLogDto.class);
        responseDto.setContent(safeBoxLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除保险柜存取分记录", notes = "删除保险柜存取分记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "保险柜存取分记录ID", required = true)
                                      @PathVariable int id) {
        safeBoxLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
    * 校验参数
    *
    * @param safeBoxLogDto 参数
    */
    private void requireParam(SafeBoxLogDto safeBoxLogDto) {

    }
}
