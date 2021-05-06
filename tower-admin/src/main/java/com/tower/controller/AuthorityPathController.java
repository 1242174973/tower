package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.AuthorityPathDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.AuthorityPathPageDto;
import com.tower.entity.AuthorityPath;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.AuthorityPathService;
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
@RequestMapping("/authorityPath")
@Api(value = "权限路径", tags = "权限路径相关请求")
public class AuthorityPathController {

    @Resource
    private AuthorityPathService authorityPathService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有权限路径", notes = "获得所有权限路径请求")
    public ResponseDto<AuthorityPathPageDto> list(@RequestBody AuthorityPathPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<AuthorityPath> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(AuthorityPath::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(AuthorityPath::getPath, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(AuthorityPath::getDescribe, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(AuthorityPath::getCreateTime);
        Page<AuthorityPath> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = authorityPathService.page(page, lambdaQueryWrapper);
        List<AuthorityPathDto> authorityPathDtoList = CopyUtil.copyList(page.getRecords(), AuthorityPathDto.class);
        pageDto.setList(authorityPathDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<AuthorityPathPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加权限路径", notes = "添加权限路径请求")
    public ResponseDto<AuthorityPathDto> add(@ApiParam(value = "权限路径信息", required = true)
                                             @RequestBody AuthorityPathDto authorityPathDto) {
        requireParam(authorityPathDto);
        AuthorityPath authorityPath = CopyUtil.copy(authorityPathDto, AuthorityPath.class);
        authorityPath.setCreateTime(LocalDateTime.now());
        authorityPathService.save(authorityPath);
        ResponseDto<AuthorityPathDto> responseDto = new ResponseDto<>();
        authorityPathDto = CopyUtil.copy(authorityPath, AuthorityPathDto.class);
        responseDto.setContent(authorityPathDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改权限路径", notes = "修改权限路径请求")
    public ResponseDto<AuthorityPathDto> edit(@ApiParam(value = "权限路径信息", required = true)
                                              @RequestBody AuthorityPathDto authorityPathDto) {
        requireParam(authorityPathDto);
        BusinessUtil.require(authorityPathDto.getId(), BusinessExceptionCode.ID);
        AuthorityPath authorityPath = authorityPathService.getById(authorityPathDto.getId());
        BusinessUtil.assertParam(authorityPath != null, "权限路径没找到");
        authorityPath.setDescribe(authorityPathDto.getDescribe());
        authorityPath.setPath(authorityPathDto.getPath());
        authorityPath.setCreateTime(LocalDateTime.now());
        authorityPathService.saveOrUpdate(authorityPath);
        ResponseDto<AuthorityPathDto> responseDto = new ResponseDto<>();
        authorityPathDto = CopyUtil.copy(authorityPath, AuthorityPathDto.class);
        responseDto.setContent(authorityPathDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除权限路径", notes = "删除权限路径请求")
    public ResponseDto<String> delete(@ApiParam(value = "权限路径ID", required = true)
                                      @PathVariable int id) {
        authorityPathService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param authorityPathDto 参数
     */
    private void requireParam(AuthorityPathDto authorityPathDto) {
        BusinessUtil.assertParam(authorityPathDto.getPath()!=null,"路径不能为空");
        BusinessUtil.assertParam(authorityPathDto.getDescribe()!=null,"描述不能为空");
    }
}
