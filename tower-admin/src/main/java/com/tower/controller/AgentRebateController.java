package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.AgentRebateDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.AgentRebatePageDto;
import com.tower.entity.AgentRebate;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.AgentRebateService;
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
@RequestMapping("/agentRebate")
@Api(value = "流水返利", tags = "流水返利相关请求")
public class AgentRebateController {

    @Resource
    private AgentRebateService agentRebateService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有流水返利", notes = "获得所有流水返利请求")
    public ResponseDto<AgentRebatePageDto> list(@RequestBody AgentRebatePageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<AgentRebate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(AgentRebate::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(AgentRebate::getAgentUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(AgentRebate::getUserId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(AgentRebate::getCreateTime);
        Page<AgentRebate> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = agentRebateService.page(page, lambdaQueryWrapper);
        List<AgentRebateDto> agentRebateDtoList = CopyUtil.copyList(page.getRecords(), AgentRebateDto.class);
        pageDto.setList(agentRebateDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<AgentRebatePageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加流水返利", notes = "添加流水返利请求")
    public ResponseDto<AgentRebateDto> add(@ApiParam(value = "流水返利信息", required = true)
                                           @RequestBody AgentRebateDto agentRebateDto) {
        requireParam(agentRebateDto);
        AgentRebate agentRebate = CopyUtil.copy(agentRebateDto, AgentRebate.class);
        agentRebate.setCreateTime(LocalDateTime.now());
        agentRebateService.save(agentRebate);
        ResponseDto<AgentRebateDto> responseDto = new ResponseDto<>();
        agentRebateDto = CopyUtil.copy(agentRebate, AgentRebateDto.class);
        responseDto.setContent(agentRebateDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改流水返利", notes = "修改流水返利请求")
    public ResponseDto<AgentRebateDto> edit(@ApiParam(value = "流水返利信息", required = true)
                                            @RequestBody AgentRebateDto agentRebateDto) {
        requireParam(agentRebateDto);
        BusinessUtil.require(agentRebateDto.getId(), BusinessExceptionCode.ID);
        AgentRebate agentRebate = agentRebateService.getById(agentRebateDto.getId());
        BusinessUtil.assertParam(agentRebate != null, "流水返利没找到");
        agentRebateService.saveOrUpdate(agentRebate);
        ResponseDto<AgentRebateDto> responseDto = new ResponseDto<>();
        agentRebateDto = CopyUtil.copy(agentRebate, AgentRebateDto.class);
        responseDto.setContent(agentRebateDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除流水返利", notes = "删除流水返利请求")
    public ResponseDto<String> delete(@ApiParam(value = "流水返利ID", required = true)
                                      @PathVariable int id) {
        agentRebateService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param agentRebateDto 参数
     */
    private void requireParam(AgentRebateDto agentRebateDto) {

    }
}
