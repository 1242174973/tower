package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.SignInDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.SignInPageDto;
import com.tower.entity.SignIn;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.SignInService;
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
@RequestMapping("/signIn")
@Api(value = "签到福利", tags = "签到福利相关请求")
public class SignInController {

    @Resource
    private SignInService signInService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有签到福利", notes = "获得所有签到福利请求")
    public ResponseDto<SignInPageDto> list(@RequestBody SignInPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<SignIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(SignIn::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(SignIn::getDay, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(SignIn::getVipLevel, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(SignIn::getVipLevel).orderByAsc(SignIn::getDay);
        Page<SignIn> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = signInService.page(page, lambdaQueryWrapper);
        List<SignInDto> signInDtoList = CopyUtil.copyList(page.getRecords(), SignInDto.class);
        pageDto.setList(signInDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<SignInPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加签到福利", notes = "添加签到福利请求")
    public ResponseDto<SignInDto> add(@ApiParam(value = "签到福利信息", required = true)
                                      @RequestBody SignInDto signInDto) {
        requireParam(signInDto);
        LambdaQueryWrapper<SignIn> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SignIn::getDay, signInDto.getDay()).eq(SignIn::getVipLevel, signInDto.getVipLevel());
        BusinessUtil.assertParam(signInService.getOne(lambdaQueryWrapper) == null, "该天福利已经存在");
        SignIn signIn = CopyUtil.copy(signInDto, SignIn.class);
        signIn.setCreateTime(LocalDateTime.now());
        signInService.save(signIn);
        ResponseDto<SignInDto> responseDto = new ResponseDto<>();
        signInDto = CopyUtil.copy(signIn, SignInDto.class);
        responseDto.setContent(signInDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改签到福利", notes = "修改签到福利请求")
    public ResponseDto<SignInDto> edit(@ApiParam(value = "签到福利信息", required = true)
                                       @RequestBody SignInDto signInDto) {
        requireParam(signInDto);
        BusinessUtil.require(signInDto.getId(), BusinessExceptionCode.ID);
        SignIn signIn = signInService.getById(signInDto.getId());
        BusinessUtil.assertParam(signIn != null, "签到福利没找到");
        signIn.setAward(signInDto.getAward());
        signIn.setAwardType(signInDto.getAwardType());
        signIn.setDay(signInDto.getDay());
        signIn.setVipLevel(signInDto.getVipLevel());
        signInService.saveOrUpdate(signIn);
        ResponseDto<SignInDto> responseDto = new ResponseDto<>();
        signInDto = CopyUtil.copy(signIn, SignInDto.class);
        responseDto.setContent(signInDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除签到福利", notes = "删除签到福利请求")
    public ResponseDto<String> delete(@ApiParam(value = "签到福利ID", required = true)
                                      @PathVariable int id) {
        signInService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param signInDto 参数
     */
    private void requireParam(SignInDto signInDto) {
        BusinessUtil.require(signInDto.getAward(), BusinessExceptionCode.AWARD);
        BusinessUtil.assertParam(signInDto.getAward().doubleValue() >= 0, "奖励不能为负数");
        BusinessUtil.require(signInDto.getAwardType(), BusinessExceptionCode.AWARD_TYPE);
        BusinessUtil.assertParam(signInDto.getAwardType() >= 0, "奖励类型不能为负数");
        BusinessUtil.require(signInDto.getDay(), BusinessExceptionCode.DAY);
        BusinessUtil.assertParam(signInDto.getDay() > 0 && signInDto.getDay() <= 15, "奖励天数在1-15天");
        BusinessUtil.require(signInDto.getVipLevel(), BusinessExceptionCode.VIP_LEVEL);
        BusinessUtil.assertParam(signInDto.getVipLevel() >= 0, "vip等级必须大于0");
    }
}
