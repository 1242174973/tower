package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.WelfareLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.WelfareLogPageDto;
import com.tower.entity.WelfareLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.WelfareLogService;
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
@RequestMapping("/welfareLog")
@Api(value = "福利记录", tags = "福利记录相关请求")
public class WelfareLogController {

    @Resource
    private WelfareLogService welfareLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有福利记录", notes = "获得所有福利记录请求")
    public ResponseDto<WelfareLogPageDto> list(@RequestBody WelfareLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<WelfareLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
               .or(queryWrapper -> queryWrapper.like(WelfareLog::getId, pageDto.getSearch()))
               .or(queryWrapper -> queryWrapper.like(WelfareLog::getUserId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(WelfareLog::getCreateTime);
        Page<WelfareLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = welfareLogService.page(page, lambdaQueryWrapper);
        List<WelfareLogDto> welfareLogDtoList = CopyUtil.copyList(page.getRecords(), WelfareLogDto.class);
        pageDto.setList(welfareLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<WelfareLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加福利记录", notes = "添加福利记录请求")
    public ResponseDto<WelfareLogDto> add(@ApiParam(value = "福利记录信息", required = true)
        @RequestBody WelfareLogDto welfareLogDto) {
        requireParam(welfareLogDto);
        WelfareLog welfareLog = CopyUtil.copy(welfareLogDto, WelfareLog.class);
        welfareLog.setCreateTime(LocalDateTime.now());
        welfareLogService.save(welfareLog);
        ResponseDto<WelfareLogDto> responseDto = new ResponseDto<>();
        welfareLogDto = CopyUtil.copy(welfareLog, WelfareLogDto.class);
        responseDto.setContent(welfareLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改福利记录", notes = "修改福利记录请求")
    public ResponseDto<WelfareLogDto> edit(@ApiParam(value = "福利记录信息", required = true)
                                       @RequestBody WelfareLogDto welfareLogDto) {
        requireParam(welfareLogDto);
        BusinessUtil.require(welfareLogDto.getId(), BusinessExceptionCode.ID);
        WelfareLog welfareLog = welfareLogService.getById(welfareLogDto.getId());
        BusinessUtil.assertParam(welfareLog != null, "福利记录没找到");
         welfareLogService.saveOrUpdate(welfareLog);
        ResponseDto<WelfareLogDto> responseDto = new ResponseDto<>();
        welfareLogDto = CopyUtil.copy(welfareLog,WelfareLogDto.class);
        responseDto.setContent(welfareLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除福利记录", notes = "删除福利记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "福利记录ID", required = true)
                                      @PathVariable int id) {
        welfareLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
    * 校验参数
    *
    * @param welfareLogDto 参数
    */
    private void requireParam(WelfareLogDto welfareLogDto) {

    }
}
