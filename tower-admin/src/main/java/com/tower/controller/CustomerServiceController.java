package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.CustomerServiceDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.CustomerServicePageDto;
import com.tower.entity.CustomerService;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.CustomerServiceService;
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
@RequestMapping("/customerService")
@Api(value = "客服地址", tags = "客服地址相关请求")
public class CustomerServiceController {

    @Resource
    private CustomerServiceService customerServiceService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有客服地址", notes = "获得所有客服地址请求")
    public ResponseDto<CustomerServicePageDto> list(@RequestBody CustomerServicePageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<CustomerService> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(CustomerService::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(CustomerService::getCreateTime);
        Page<CustomerService> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = customerServiceService.page(page, lambdaQueryWrapper);
        List<CustomerServiceDto> customerServiceDtoList = CopyUtil.copyList(page.getRecords(), CustomerServiceDto.class);
        pageDto.setList(customerServiceDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<CustomerServicePageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加客服地址", notes = "添加客服地址请求")
    public ResponseDto<CustomerServiceDto> add(@ApiParam(value = "客服地址信息", required = true)
                                               @RequestBody CustomerServiceDto customerServiceDto) {
        requireParam(customerServiceDto);
        CustomerService customerService = CopyUtil.copy(customerServiceDto, CustomerService.class);
        customerService.setCreateTime(LocalDateTime.now());
        customerServiceService.save(customerService);
        ResponseDto<CustomerServiceDto> responseDto = new ResponseDto<>();
        customerServiceDto = CopyUtil.copy(customerService, CustomerServiceDto.class);
        responseDto.setContent(customerServiceDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改客服地址", notes = "修改客服地址请求")
    public ResponseDto<CustomerServiceDto> edit(@ApiParam(value = "客服地址信息", required = true)
                                                @RequestBody CustomerServiceDto customerServiceDto) {
        requireParam(customerServiceDto);
        BusinessUtil.require(customerServiceDto.getId(), BusinessExceptionCode.ID);
        CustomerService customerService = customerServiceService.getById(customerServiceDto.getId());
        BusinessUtil.assertParam(customerService != null, "客服地址没找到");
        customerService.setPath(customerServiceDto.getPath());
        customerService.setCreateTime(LocalDateTime.now());
        customerServiceService.saveOrUpdate(customerService);
        ResponseDto<CustomerServiceDto> responseDto = new ResponseDto<>();
        customerServiceDto = CopyUtil.copy(customerService, CustomerServiceDto.class);
        responseDto.setContent(customerServiceDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除客服地址", notes = "删除客服地址请求")
    public ResponseDto<String> delete(@ApiParam(value = "客服地址ID", required = true)
                                      @PathVariable int id) {
        customerServiceService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param customerServiceDto 参数
     */
    private void requireParam(CustomerServiceDto customerServiceDto) {
        BusinessUtil.require(customerServiceDto.getPath(), "地址不能为空");
    }
}
