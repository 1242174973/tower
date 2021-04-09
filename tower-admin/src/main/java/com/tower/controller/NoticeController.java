package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.NoticeDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.NoticePageDto;
import com.tower.entity.Notice;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.NoticeService;
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
@RequestMapping("/notice")
@Api(value = "公告", tags = "公告相关请求")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有公告", notes = "获得所有公告请求")
    public ResponseDto<NoticePageDto> list(@RequestBody NoticePageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<Notice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(Notice::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Notice::getContent, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(Notice::getCreateTime);
        Page<Notice> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = noticeService.page(page, lambdaQueryWrapper);
        List<NoticeDto> noticeDtoList = CopyUtil.copyList(page.getRecords(), NoticeDto.class);
        pageDto.setList(noticeDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<NoticePageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加公告", notes = "添加公告请求")
    public ResponseDto<NoticeDto> add(@ApiParam(value = "公告信息", required = true)
                                      @RequestBody NoticeDto noticeDto) {
        requireParam(noticeDto);
        Notice notice = CopyUtil.copy(noticeDto, Notice.class);
        notice.setCreateTime(LocalDateTime.now());
        noticeService.save(notice);
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();
        noticeDto = CopyUtil.copy(notice, NoticeDto.class);
        responseDto.setContent(noticeDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改公告", notes = "修改公告请求")
    public ResponseDto<NoticeDto> edit(@ApiParam(value = "公告信息", required = true)
                                       @RequestBody NoticeDto noticeDto) {
        requireParam(noticeDto);
        BusinessUtil.require(noticeDto.getId(), BusinessExceptionCode.ID);
        Notice notice = noticeService.getById(noticeDto.getId());
        BusinessUtil.assertParam(notice != null, "公告没找到");
        notice.setContent(noticeDto.getContent());
        notice.setCreateTime(LocalDateTime.now());
        noticeService.saveOrUpdate(notice);
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();
        noticeDto = CopyUtil.copy(notice, NoticeDto.class);
        responseDto.setContent(noticeDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除公告", notes = "删除公告请求")
    public ResponseDto<String> delete(@ApiParam(value = "公告ID", required = true)
                                      @PathVariable int id) {
        noticeService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param noticeDto 参数
     */
    private void requireParam(NoticeDto noticeDto) {
        BusinessUtil.assertParam(noticeDto.getContent() != null, "公告内容不能为空");
    }
}
