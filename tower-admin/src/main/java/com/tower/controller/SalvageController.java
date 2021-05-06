package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.SalvageDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.SalvagePageDto;
import com.tower.entity.Salvage;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.SalvageService;
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
@RequestMapping("/salvage")
@Api(value = "救助福利", tags = "救助福利相关请求")
public class SalvageController {

    @Resource
    private SalvageService salvageService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有救助福利", notes = "获得所有救助福利请求")
    public ResponseDto<SalvagePageDto> list(@RequestBody SalvagePageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<Salvage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(Salvage::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(Salvage::getGetSalvage, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(Salvage::getProfit, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.eq(Salvage::getSalvage, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Salvage::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(Salvage::getCreateTime);
        Page<Salvage> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = salvageService.page(page, lambdaQueryWrapper);
        List<SalvageDto> salvageDtoList = CopyUtil.copyList(page.getRecords(), SalvageDto.class);
        pageDto.setList(salvageDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<SalvagePageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加救助福利", notes = "添加救助福利请求")
    public ResponseDto<SalvageDto> add(@ApiParam(value = "救助福利信息", required = true)
                                       @RequestBody SalvageDto salvageDto) {
        requireParam(salvageDto);
        Salvage salvage = CopyUtil.copy(salvageDto, Salvage.class);
        salvage.setCreateTime(LocalDateTime.now());
        salvageService.save(salvage);
        ResponseDto<SalvageDto> responseDto = new ResponseDto<>();
        salvageDto = CopyUtil.copy(salvage, SalvageDto.class);
        responseDto.setContent(salvageDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改救助福利", notes = "修改救助福利请求")
    public ResponseDto<SalvageDto> edit(@ApiParam(value = "救助福利信息", required = true)
                                        @RequestBody SalvageDto salvageDto) {
        requireParam(salvageDto);
        BusinessUtil.require(salvageDto.getId(), BusinessExceptionCode.ID);
        Salvage salvage = salvageService.getById(salvageDto.getId());
        BusinessUtil.assertParam(salvage != null, "救助福利没找到");
        salvageService.saveOrUpdate(salvage);
        ResponseDto<SalvageDto> responseDto = new ResponseDto<>();
        salvageDto = CopyUtil.copy(salvage, SalvageDto.class);
        responseDto.setContent(salvageDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除救助福利", notes = "删除救助福利请求")
    public ResponseDto<String> delete(@ApiParam(value = "救助福利ID", required = true)
                                      @PathVariable int id) {
        salvageService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param salvageDto 参数
     */
    private void requireParam(SalvageDto salvageDto) {

    }
}
