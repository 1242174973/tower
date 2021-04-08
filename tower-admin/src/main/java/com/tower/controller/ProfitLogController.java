package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.ProfitLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.ProfitLogPageDto;
import com.tower.entity.ProfitLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.ProfitLogService;
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
@RequestMapping("/profitLog")
@Api(value = "代理盈利", tags = "代理盈利相关请求")
public class ProfitLogController {

    @Resource
    private ProfitLogService profitLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有代理盈利", notes = "获得所有代理盈利请求")
    public ResponseDto<ProfitLogPageDto> list(@RequestBody ProfitLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<ProfitLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(ProfitLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ProfitLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ProfitLog::getUserId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(ProfitLog::getCreateTime);
        Page<ProfitLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = profitLogService.page(page, lambdaQueryWrapper);
        List<ProfitLogDto> profitLogDtoList = CopyUtil.copyList(page.getRecords(), ProfitLogDto.class);
        pageDto.setList(profitLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<ProfitLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加代理盈利", notes = "添加代理盈利请求")
    public ResponseDto<ProfitLogDto> add(@ApiParam(value = "代理盈利信息", required = true)
                                         @RequestBody ProfitLogDto profitLogDto) {
        requireParam(profitLogDto);
        ProfitLog profitLog = CopyUtil.copy(profitLogDto, ProfitLog.class);
        profitLog.setCreateTime(LocalDateTime.now());
        profitLogService.save(profitLog);
        ResponseDto<ProfitLogDto> responseDto = new ResponseDto<>();
        profitLogDto = CopyUtil.copy(profitLog, ProfitLogDto.class);
        responseDto.setContent(profitLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改代理盈利", notes = "修改代理盈利请求")
    public ResponseDto<ProfitLogDto> edit(@ApiParam(value = "代理盈利信息", required = true)
                                          @RequestBody ProfitLogDto profitLogDto) {
        requireParam(profitLogDto);
        BusinessUtil.require(profitLogDto.getId(), BusinessExceptionCode.ID);
        ProfitLog profitLog = profitLogService.getById(profitLogDto.getId());
        BusinessUtil.assertParam(profitLog != null, "代理盈利没找到");
        profitLogService.saveOrUpdate(profitLog);
        ResponseDto<ProfitLogDto> responseDto = new ResponseDto<>();
        profitLogDto = CopyUtil.copy(profitLog, ProfitLogDto.class);
        responseDto.setContent(profitLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除代理盈利", notes = "删除代理盈利请求")
    public ResponseDto<String> delete(@ApiParam(value = "代理盈利ID", required = true)
                                      @PathVariable int id) {
        profitLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param profitLogDto 参数
     */
    private void requireParam(ProfitLogDto profitLogDto) {

    }
}
