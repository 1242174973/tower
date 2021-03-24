package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.TopUpLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.TopUpLogPageDto;
import com.tower.entity.TopUpLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.TopUpLogService;
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
@RequestMapping("/topUpLog")
@Api(value = "充值记录", tags = "充值记录相关请求")
public class TopUpLogController {

    @Resource
    private TopUpLogService topUpLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有充值记录", notes = "获得所有充值记录请求")
    public ResponseDto<TopUpLogPageDto> list(@RequestBody TopUpLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<TopUpLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                .or(queryWrapper -> queryWrapper.like(TopUpLog::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(TopUpLog::getCreateTime);
        Page<TopUpLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = topUpLogService.page(page, lambdaQueryWrapper);
        List<TopUpLogDto> topUpLogDtoList = CopyUtil.copyList(page.getRecords(), TopUpLogDto.class);
        pageDto.setList(topUpLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<TopUpLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加充值记录", notes = "添加充值记录请求")
    public ResponseDto<TopUpLogDto> add(@ApiParam(value = "充值记录信息", required = true)
        @RequestBody TopUpLogDto topUpLogDto) {
        requireParam(topUpLogDto);
        TopUpLog topUpLog = CopyUtil.copy(topUpLogDto, TopUpLog.class);
        topUpLog.setCreateTime(LocalDateTime.now());
        topUpLogService.save(topUpLog);
        ResponseDto<TopUpLogDto> responseDto = new ResponseDto<>();
        topUpLogDto = CopyUtil.copy(topUpLog, TopUpLogDto.class);
        responseDto.setContent(topUpLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改充值记录", notes = "修改充值记录请求")
    public ResponseDto<TopUpLogDto> edit(@ApiParam(value = "充值记录信息", required = true)
                                       @RequestBody TopUpLogDto topUpLogDto) {
        requireParam(topUpLogDto);
        BusinessUtil.require(topUpLogDto.getId(), BusinessExceptionCode.ID);
        TopUpLog topUpLog = topUpLogService.getById(topUpLogDto.getId());
        BusinessUtil.assertParam(topUpLog != null, "充值记录没找到");
         topUpLogService.saveOrUpdate(topUpLog);
        ResponseDto<TopUpLogDto> responseDto = new ResponseDto<>();
        topUpLogDto = CopyUtil.copy(topUpLog,TopUpLogDto.class);
        responseDto.setContent(topUpLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除充值记录", notes = "删除充值记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "充值记录ID", required = true)
                                      @PathVariable int id) {
        topUpLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
    * 校验参数
    *
    * @param topUpLogDto 参数
    */
    private void requireParam(TopUpLogDto topUpLogDto) {

    }
}
