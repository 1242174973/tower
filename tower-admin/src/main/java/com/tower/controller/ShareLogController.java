package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.ShareLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.ShareLogPageDto;
import com.tower.entity.ShareLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.ShareLogService;
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
@RequestMapping("/shareLog")
@Api(value = "分享记录", tags = "分享记录相关请求")
public class ShareLogController {

    @Resource
    private ShareLogService shareLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有分享记录", notes = "获得所有分享记录请求")
    public ResponseDto<ShareLogPageDto> list(@RequestBody ShareLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<ShareLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(ShareLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ShareLog::getShareId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(ShareLog::getYieldId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(ShareLog::getCreateTime);
        Page<ShareLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = shareLogService.page(page, lambdaQueryWrapper);
        List<ShareLogDto> shareLogDtoList = CopyUtil.copyList(page.getRecords(), ShareLogDto.class);
        pageDto.setList(shareLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<ShareLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加分享记录", notes = "添加分享记录请求")
    public ResponseDto<ShareLogDto> add(@ApiParam(value = "分享记录信息", required = true)
                                        @RequestBody ShareLogDto shareLogDto) {
        requireParam(shareLogDto);
        ShareLog shareLog = CopyUtil.copy(shareLogDto, ShareLog.class);
        shareLog.setCreateTime(LocalDateTime.now());
        shareLogService.save(shareLog);
        ResponseDto<ShareLogDto> responseDto = new ResponseDto<>();
        shareLogDto = CopyUtil.copy(shareLog, ShareLogDto.class);
        responseDto.setContent(shareLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改分享记录", notes = "修改分享记录请求")
    public ResponseDto<ShareLogDto> edit(@ApiParam(value = "分享记录信息", required = true)
                                         @RequestBody ShareLogDto shareLogDto) {
        requireParam(shareLogDto);
        BusinessUtil.require(shareLogDto.getId(), BusinessExceptionCode.ID);
        ShareLog shareLog = shareLogService.getById(shareLogDto.getId());
        BusinessUtil.assertParam(shareLog != null, "分享记录没找到");
        shareLogService.saveOrUpdate(shareLog);
        ResponseDto<ShareLogDto> responseDto = new ResponseDto<>();
        shareLogDto = CopyUtil.copy(shareLog, ShareLogDto.class);
        responseDto.setContent(shareLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除分享记录", notes = "删除分享记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "分享记录ID", required = true)
                                      @PathVariable int id) {
        shareLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param shareLogDto 参数
     */
    private void requireParam(ShareLogDto shareLogDto) {

    }
}
