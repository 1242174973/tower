package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.CustomerServiceDto;
import com.tower.dto.EditionEtcDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.CustomerService;
import com.tower.entity.EditionEtc;
import com.tower.service.CustomerServiceService;
import com.tower.service.EditionEtcService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xxxx
 * @date 2021/4/12 13:51
 */
@RestController
@RequestMapping("/customer")
@Api(value = "客服信息", tags = "客服信息相关请求")
public class CustomerServiceController {
    @Resource
    private CustomerServiceService customerServiceService;

    @GetMapping("/getCustomer")
    @ApiOperation(value = "获得客服信息", notes = "无参")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<CustomerServiceDto> getCustomer() {
        List<CustomerService> list = customerServiceService.list();
        BusinessUtil.assertParam(list.size() > 0, "没有找到客服信息");
        CustomerServiceDto copy = CopyUtil.copy(list.get(0), CustomerServiceDto.class);
        ResponseDto<CustomerServiceDto> responseDto = new ResponseDto<>();
        responseDto.setContent(copy);
        return responseDto;
    }

}
