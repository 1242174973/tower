package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.ExtracLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.ExtracLogPageDto;
import com.tower.entity.ExtracLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.ExtracLogService;
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
@RequestMapping("/extracLog")
@Api(value = "提取记录", tags = "提取记录相关请求")
public class ExtracLogController {

    @Resource
    private ExtracLogService extracLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有提取记录", notes = "获得所有提取记录请求")
    public ResponseDto<ExtracLogPageDto> list(@RequestBody ExtracLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<ExtracLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(ExtracLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ExtracLog::getExtracId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(ExtracLog::getCreateTime);
        Page<ExtracLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = extracLogService.page(page, lambdaQueryWrapper);
        List<ExtracLogDto> extracLogDtoList = CopyUtil.copyList(page.getRecords(), ExtracLogDto.class);
        pageDto.setList(extracLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<ExtracLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加提取记录", notes = "添加提取记录请求")
    public ResponseDto<ExtracLogDto> add(@ApiParam(value = "提取记录信息", required = true)
                                         @RequestBody ExtracLogDto extracLogDto) {
        requireParam(extracLogDto);
        ExtracLog extracLog = CopyUtil.copy(extracLogDto, ExtracLog.class);
        extracLog.setCreateTime(LocalDateTime.now());
        extracLogService.save(extracLog);
        ResponseDto<ExtracLogDto> responseDto = new ResponseDto<>();
        extracLogDto = CopyUtil.copy(extracLog, ExtracLogDto.class);
        responseDto.setContent(extracLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改提取记录", notes = "修改提取记录请求")
    public ResponseDto<ExtracLogDto> edit(@ApiParam(value = "提取记录信息", required = true)
                                          @RequestBody ExtracLogDto extracLogDto) {
        requireParam(extracLogDto);
        BusinessUtil.require(extracLogDto.getId(), BusinessExceptionCode.ID);
        ExtracLog extracLog = extracLogService.getById(extracLogDto.getId());
        BusinessUtil.assertParam(extracLog != null, "提取记录没找到");
        extracLogService.saveOrUpdate(extracLog);
        ResponseDto<ExtracLogDto> responseDto = new ResponseDto<>();
        extracLogDto = CopyUtil.copy(extracLog, ExtracLogDto.class);
        responseDto.setContent(extracLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除提取记录", notes = "删除提取记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "提取记录ID", required = true)
                                      @PathVariable int id) {
        extracLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param extracLogDto 参数
     */
    private void requireParam(ExtracLogDto extracLogDto) {

    }
}
