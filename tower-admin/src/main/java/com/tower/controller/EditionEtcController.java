package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.EditionEtcDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.EditionEtcPageDto;
import com.tower.entity.EditionEtc;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.EditionEtcService;
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
@RequestMapping("/editionEtc")
@Api(value = "版本管理", tags = "版本管理相关请求")
public class EditionEtcController {

    @Resource
    private EditionEtcService editionEtcService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有版本管理", notes = "获得所有版本管理请求")
    public ResponseDto<EditionEtcPageDto> list(@RequestBody EditionEtcPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<EditionEtc> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(EditionEtc::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(EditionEtc::getCreateTime);
        Page<EditionEtc> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = editionEtcService.page(page, lambdaQueryWrapper);
        List<EditionEtcDto> editionEtcDtoList = CopyUtil.copyList(page.getRecords(), EditionEtcDto.class);
        pageDto.setList(editionEtcDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<EditionEtcPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加版本管理", notes = "添加版本管理请求")
    public ResponseDto<EditionEtcDto> add(@ApiParam(value = "版本管理信息", required = true)
                                          @RequestBody EditionEtcDto editionEtcDto) {
        requireParam(editionEtcDto);
        EditionEtc editionEtc = CopyUtil.copy(editionEtcDto, EditionEtc.class);
        editionEtc.setCreateTime(LocalDateTime.now());
        editionEtcService.save(editionEtc);
        ResponseDto<EditionEtcDto> responseDto = new ResponseDto<>();
        editionEtcDto = CopyUtil.copy(editionEtc, EditionEtcDto.class);
        responseDto.setContent(editionEtcDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改版本管理", notes = "修改版本管理请求")
    public ResponseDto<EditionEtcDto> edit(@ApiParam(value = "版本管理信息", required = true)
                                           @RequestBody EditionEtcDto editionEtcDto) {
        requireParam(editionEtcDto);
        BusinessUtil.require(editionEtcDto.getId(), BusinessExceptionCode.ID);
        EditionEtc editionEtc = editionEtcService.getById(editionEtcDto.getId());
        BusinessUtil.assertParam(editionEtc != null, "版本管理没找到");
        editionEtc.setDescription(editionEtcDto.getDescription())
                .setDimensional(editionEtcDto.getDimensional())
                .setUrl(editionEtcDto.getUrl())
                .setNewest(editionEtcDto.getNewest())
                .setForceVersion(editionEtcDto.getForceVersion());
        editionEtcService.saveOrUpdate(editionEtc);
        ResponseDto<EditionEtcDto> responseDto = new ResponseDto<>();
        editionEtcDto = CopyUtil.copy(editionEtc, EditionEtcDto.class);
        responseDto.setContent(editionEtcDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除版本管理", notes = "删除版本管理请求")
    public ResponseDto<String> delete(@ApiParam(value = "版本管理ID", required = true)
                                      @PathVariable int id) {
        editionEtcService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param editionEtcDto 参数
     */
    private void requireParam(EditionEtcDto editionEtcDto) {
        BusinessUtil.require(editionEtcDto.getDescription(), "版本描述不能为空");
        BusinessUtil.require(editionEtcDto.getNewest(), "最新版本不能为空");
        BusinessUtil.require(editionEtcDto.getForceVersion(), "强制更新版本不能为空");
        BusinessUtil.require(editionEtcDto.getUrl(), "下载地址不能为空");
        BusinessUtil.require(editionEtcDto.getDimensional(), "二维码地址不能为空");
    }
}
