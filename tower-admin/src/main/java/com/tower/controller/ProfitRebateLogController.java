package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.ProfitRebateLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.ProfitRebateLogPageDto;
import com.tower.entity.ProfitRebateLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.ProfitRebateLogService;
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
@RequestMapping("/profitRebateLog")
@Api(value = "代理盈利结算", tags = "代理盈利结算相关请求")
public class ProfitRebateLogController {

    @Resource
    private ProfitRebateLogService profitRebateLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有代理盈利结算", notes = "获得所有代理盈利结算请求")
    public ResponseDto<ProfitRebateLogPageDto> list(@RequestBody ProfitRebateLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<ProfitRebateLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(ProfitRebateLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ProfitRebateLog::getUserId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(ProfitRebateLog::getCreateTime);
        Page<ProfitRebateLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = profitRebateLogService.page(page, lambdaQueryWrapper);
        List<ProfitRebateLogDto> profitRebateLogDtoList = CopyUtil.copyList(page.getRecords(), ProfitRebateLogDto.class);
        pageDto.setList(profitRebateLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<ProfitRebateLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加代理盈利结算", notes = "添加代理盈利结算请求")
    public ResponseDto<ProfitRebateLogDto> add(@ApiParam(value = "代理盈利结算信息", required = true)
                                               @RequestBody ProfitRebateLogDto profitRebateLogDto) {
        requireParam(profitRebateLogDto);
        ProfitRebateLog profitRebateLog = CopyUtil.copy(profitRebateLogDto, ProfitRebateLog.class);
        profitRebateLog.setCreateTime(LocalDateTime.now());
        profitRebateLogService.save(profitRebateLog);
        ResponseDto<ProfitRebateLogDto> responseDto = new ResponseDto<>();
        profitRebateLogDto = CopyUtil.copy(profitRebateLog, ProfitRebateLogDto.class);
        responseDto.setContent(profitRebateLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改代理盈利结算", notes = "修改代理盈利结算请求")
    public ResponseDto<ProfitRebateLogDto> edit(@ApiParam(value = "代理盈利结算信息", required = true)
                                                @RequestBody ProfitRebateLogDto profitRebateLogDto) {
        requireParam(profitRebateLogDto);
        BusinessUtil.require(profitRebateLogDto.getId(), BusinessExceptionCode.ID);
        ProfitRebateLog profitRebateLog = profitRebateLogService.getById(profitRebateLogDto.getId());
        BusinessUtil.assertParam(profitRebateLog != null, "代理盈利结算没找到");
        profitRebateLogService.saveOrUpdate(profitRebateLog);
        ResponseDto<ProfitRebateLogDto> responseDto = new ResponseDto<>();
        profitRebateLogDto = CopyUtil.copy(profitRebateLog, ProfitRebateLogDto.class);
        responseDto.setContent(profitRebateLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除代理盈利结算", notes = "删除代理盈利结算请求")
    public ResponseDto<String> delete(@ApiParam(value = "代理盈利结算ID", required = true)
                                      @PathVariable int id) {
        profitRebateLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param profitRebateLogDto 参数
     */
    private void requireParam(ProfitRebateLogDto profitRebateLogDto) {

    }
}
