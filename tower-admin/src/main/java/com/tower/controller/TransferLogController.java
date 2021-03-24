package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.TransferLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.TransferLogPageDto;
import com.tower.entity.TransferLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.TransferLogService;
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
@RequestMapping("/transferLog")
@Api(value = "转账记录", tags = "转账记录相关请求")
public class TransferLogController {

    @Resource
    private TransferLogService transferLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有转账记录", notes = "获得所有转账记录请求")
    public ResponseDto<TransferLogPageDto> list(@RequestBody TransferLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<TransferLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                .or(queryWrapper -> queryWrapper.like(TransferLog::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(TransferLog::getCreateTime);
        Page<TransferLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = transferLogService.page(page, lambdaQueryWrapper);
        List<TransferLogDto> transferLogDtoList = CopyUtil.copyList(page.getRecords(), TransferLogDto.class);
        pageDto.setList(transferLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<TransferLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加转账记录", notes = "添加转账记录请求")
    public ResponseDto<TransferLogDto> add(@ApiParam(value = "转账记录信息", required = true)
        @RequestBody TransferLogDto transferLogDto) {
        requireParam(transferLogDto);
        TransferLog transferLog = CopyUtil.copy(transferLogDto, TransferLog.class);
        transferLog.setCreateTime(LocalDateTime.now());
        transferLogService.save(transferLog);
        ResponseDto<TransferLogDto> responseDto = new ResponseDto<>();
        transferLogDto = CopyUtil.copy(transferLog, TransferLogDto.class);
        responseDto.setContent(transferLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改转账记录", notes = "修改转账记录请求")
    public ResponseDto<TransferLogDto> edit(@ApiParam(value = "转账记录信息", required = true)
                                       @RequestBody TransferLogDto transferLogDto) {
        requireParam(transferLogDto);
        BusinessUtil.require(transferLogDto.getId(), BusinessExceptionCode.ID);
        TransferLog transferLog = transferLogService.getById(transferLogDto.getId());
        BusinessUtil.assertParam(transferLog != null, "转账记录没找到");
         transferLogService.saveOrUpdate(transferLog);
        ResponseDto<TransferLogDto> responseDto = new ResponseDto<>();
        transferLogDto = CopyUtil.copy(transferLog,TransferLogDto.class);
        responseDto.setContent(transferLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除转账记录", notes = "删除转账记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "转账记录ID", required = true)
                                      @PathVariable int id) {
        transferLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
    * 校验参数
    *
    * @param transferLogDto 参数
    */
    private void requireParam(TransferLogDto transferLogDto) {

    }
}
