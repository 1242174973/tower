package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.UserBankCardDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.UserBankCardPageDto;
import com.tower.entity.UserBankCard;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.UserBankCardService;
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
@RequestMapping("/userBankCard")
@Api(value = "玩家绑卡信息", tags = "玩家绑卡信息相关请求")
public class UserBankCardController {

    @Resource
    private UserBankCardService userBankCardService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有玩家绑卡信息", notes = "获得所有玩家绑卡信息请求")
    public ResponseDto<UserBankCardPageDto> list(@RequestBody UserBankCardPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<UserBankCard> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getUserId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getBankCardName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getBankCardNum, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getBank, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getSubBranch, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getProvince,pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getCity, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(UserBankCard::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(UserBankCard::getCreateTime);
        Page<UserBankCard> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = userBankCardService.page(page, lambdaQueryWrapper);
        List<UserBankCardDto> userBankCardDtoList = CopyUtil.copyList(page.getRecords(), UserBankCardDto.class);
        pageDto.setList(userBankCardDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<UserBankCardPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加玩家绑卡信息", notes = "添加玩家绑卡信息请求")
    public ResponseDto<UserBankCardDto> add(@ApiParam(value = "玩家绑卡信息信息", required = true)
                                            @RequestBody UserBankCardDto userBankCardDto) {
        requireParam(userBankCardDto);
        UserBankCard userBankCard = CopyUtil.copy(userBankCardDto, UserBankCard.class);
        userBankCard.setCreateTime(LocalDateTime.now());
        userBankCardService.save(userBankCard);
        ResponseDto<UserBankCardDto> responseDto = new ResponseDto<>();
        userBankCardDto = CopyUtil.copy(userBankCard, UserBankCardDto.class);
        responseDto.setContent(userBankCardDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改玩家绑卡信息", notes = "修改玩家绑卡信息请求")
    public ResponseDto<UserBankCardDto> edit(@ApiParam(value = "玩家绑卡信息信息", required = true)
                                             @RequestBody UserBankCardDto userBankCardDto) {
        requireParam(userBankCardDto);
        BusinessUtil.require(userBankCardDto.getId(), BusinessExceptionCode.ID);
        UserBankCard userBankCard = userBankCardService.getById(userBankCardDto.getId());
        BusinessUtil.assertParam(userBankCard != null, "玩家绑卡信息没找到");
        userBankCardService.saveOrUpdate(userBankCard);
        ResponseDto<UserBankCardDto> responseDto = new ResponseDto<>();
        userBankCardDto = CopyUtil.copy(userBankCard, UserBankCardDto.class);
        responseDto.setContent(userBankCardDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除玩家绑卡信息", notes = "删除玩家绑卡信息请求")
    public ResponseDto<String> delete(@ApiParam(value = "玩家绑卡信息ID", required = true)
                                      @PathVariable int id) {
        userBankCardService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param userBankCardDto 参数
     */
    private void requireParam(UserBankCardDto userBankCardDto) {

    }
}
