package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.ResponseDto;
import com.tower.dto.UserBankCardDto;
import com.tower.entity.Player;
import com.tower.entity.UserBankCard;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.UserBankCardService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/19 11:21
 */
@RestController
@RequestMapping("/userBankCard")
@Api(value = "玩家银行卡", tags = "玩家银行卡信息相关请求")
public class UserBankCardController {

    @Resource
    private UserBankCardService userBankCardService;

    @GetMapping("/bindBankCard")
    @ApiOperation(value = "绑定银行卡", notes = "参数 银行卡持卡人 银行卡号 银行 支行 省 市")
    public ResponseDto<UserBankCardDto> bindBankCard(Player player,
                                                     @ApiParam(value = "绑定银行卡信息", required = true)
                                                     @RequestBody UserBankCardDto userBankCardDto) {
        BusinessUtil.require(userBankCardDto.getBankCardName(), BusinessExceptionCode.BANK_CARD_NAME);
        BusinessUtil.length(userBankCardDto.getBankCardName(), BusinessExceptionCode.BANK_CARD_NAME, 2, 5);
        BusinessUtil.require(userBankCardDto.getBankCardNum(), BusinessExceptionCode.BANK_CARD_NUM);
        BusinessUtil.length(userBankCardDto.getBankCardNum(), BusinessExceptionCode.BANK_CARD_NUM, 15, 20);
        BusinessUtil.require(userBankCardDto.getBank(), BusinessExceptionCode.BANK);
        BusinessUtil.length(userBankCardDto.getBank(), BusinessExceptionCode.BANK, 2, 15);
        BusinessUtil.require(userBankCardDto.getSubBranch(), BusinessExceptionCode.SUB_BRANCH);
        BusinessUtil.length(userBankCardDto.getSubBranch(), BusinessExceptionCode.SUB_BRANCH, 2, 15);
        BusinessUtil.require(userBankCardDto.getProvince(), BusinessExceptionCode.PROVINCE);
        BusinessUtil.length(userBankCardDto.getProvince(), BusinessExceptionCode.PROVINCE, 2, 10);
        BusinessUtil.require(userBankCardDto.getCity(), BusinessExceptionCode.CITY);
        BusinessUtil.length(userBankCardDto.getCity(), BusinessExceptionCode.CITY, 2, 10);
        UserBankCard userBankCard = CopyUtil.copy(userBankCardDto, UserBankCard.class);
        userBankCard.setCreateTime(LocalDateTime.now());
        userBankCard.setUserId(player.getId());
        LambdaQueryWrapper<UserBankCard> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBankCard::getUserId, player.getId());
        userBankCardService.saveOrUpdate(userBankCard, lambdaQueryWrapper);
        ResponseDto<UserBankCardDto> responseDto = new ResponseDto<>();
        responseDto.setContent(userBankCardDto);
        return responseDto;
    }
}
