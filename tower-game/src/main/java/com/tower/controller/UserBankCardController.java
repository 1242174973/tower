package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.*;
import com.tower.dto.page.WithdrawLogPageDto;
import com.tower.entity.*;
import com.tower.enums.WelfareModelEnum;
import com.tower.enums.WelfareTypeEnum;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.UserBankCardService;
import com.tower.service.UserWithdrawConfigService;
import com.tower.service.WelfareLogService;
import com.tower.service.WithdrawLogService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtil;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * @author xxxx
 * @date 2021/3/19 11:21
 */
@RestController
@RequestMapping("/userBankCard")
@Api(value = "玩家银行卡", tags = "玩家银行卡信息相关请求")
public class UserBankCardController {

    @Resource
    private UserBankCardService userBankCardService;

    @Resource
    private UserWithdrawConfigService userWithdrawConfigService;

    @Resource
    private WithdrawLogService withdrawLogService;


    @PostMapping("/bindBankCard")
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

    @GetMapping("/userBankCard")
    @ApiOperation(value = "获得银行卡信息", notes = "无需参数")
    public ResponseDto<UserBankCardDto> userBankCard(Player player) {
        LambdaQueryWrapper<UserBankCard> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBankCard::getUserId, player.getId());
        UserBankCard one = userBankCardService.getOne(lambdaQueryWrapper);
        ResponseDto<UserBankCardDto> responseDto = new ResponseDto<>();
        UserBankCardDto userWithdrawConfigDto = CopyUtil.copy(one, UserBankCardDto.class);
        responseDto.setContent(userWithdrawConfigDto);
        return responseDto;
    }

    @GetMapping("/userWithdrawConfig")
    @ApiOperation(value = "获得提现配置", notes = "无需参数")
    public ResponseDto<UserWithdrawConfigDto> userWelfareConfig(Player player) {
        LambdaQueryWrapper<UserWithdrawConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserWithdrawConfig::getUserId, player.getId());
        UserWithdrawConfig one = userWithdrawConfigService.getOne(lambdaQueryWrapper);
        ResponseDto<UserWithdrawConfigDto> responseDto = new ResponseDto<>();
        UserWithdrawConfigDto userWithdrawConfigDto = CopyUtil.copy(one, UserWithdrawConfigDto.class);
        responseDto.setContent(userWithdrawConfigDto);
        return responseDto;
    }

    @PostMapping("/userWithdrawConfig/{money}")
    @ApiOperation(value = "提现", notes = "参数 提现金额")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> withdraw(Player player,
                                           @ApiParam(value = "提现金额", required = true)
                                           @PathVariable double money) {
        BusinessUtil.assertParam(player.getPresent() <= 0, "您还需再参与投注【" + player.getPresent() + ".00元】才能提现，如有疑问请联系客服人员");
        List<WithdrawLog> list = withdrawLogService.list(new LambdaQueryWrapper<WithdrawLog>().eq(WithdrawLog::getState, 0));
        BusinessUtil.assertParam(list == null || list.size() <= 0, "你有一个订单正在处理，请稍后再试");
        BusinessUtil.assertParam(money >= 100 && money < 49999, "提现金额在100-49999");
        BusinessUtil.assertParam(player.getMoney().doubleValue() > money, "玩家余额不足" + money);
        LambdaQueryWrapper<UserBankCard> userBankCardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userBankCardLambdaQueryWrapper.eq(UserBankCard::getUserId, player.getId());
        UserBankCard userBankCard = userBankCardService.getOne(userBankCardLambdaQueryWrapper);
        BusinessUtil.assertParam(userBankCard != null, "玩家没有绑定银行卡");
        LambdaQueryWrapper<UserWithdrawConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserWithdrawConfig::getUserId, player.getId());
        UserWithdrawConfig one = userWithdrawConfigService.getOne(lambdaQueryWrapper);
        BusinessUtil.assertParam(one.getTodayWithdrawSize() > 0, "提现次数不足");
        BusinessUtil.assertParam(one.getTodayWithdrawMoney() >= money, "提现额度不足");
        player.setMoney(player.getMoney().subtract(BigDecimal.valueOf(money)));
        one.setTodayWithdrawSize(one.getTodayWithdrawSize() - 1);
        one.setTodayWithdrawMoney(one.getTodayWithdrawMoney() - money);
        WithdrawLog withdrawLog = new WithdrawLog().setUserId(player.getId())
                .setOrderId(DateUtils.getNowDate() + (new Random().nextInt(900000) + 100000))
                .setBankCardName(userBankCard.getBankCardName())
                .setState(0)
                .setBankCardNum(userBankCard.getBankCardNum())
                .setCreateTime(LocalDateTime.now())
                .setWithdrawMoney(BigDecimal.valueOf(money))
                .setServiceCharge(BigDecimal.valueOf(money * one.getServiceCharge() / 100));
        withdrawLogService.save(withdrawLog);
        userWithdrawConfigService.saveOrUpdate(one);
        return PlayerUtils.getPlayerDtoResponseDto(player);
    }

    @PostMapping("/userWithdrawList")
    @ApiOperation(value = "提现记录", notes = "参数 分页参数")
    public ResponseDto<WithdrawLogPageDto> withdrawList(Player player,
                                                        @ApiParam(value = "获取信息", required = true)
                                                        @RequestBody WithdrawLogPageDto withdrawLogPageDto) {
        BusinessUtil.assertParam(withdrawLogPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(withdrawLogPageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<WithdrawLogPageDto> responseDto = new ResponseDto<>();
        Page<WithdrawLog> page = new Page<>(withdrawLogPageDto.getPage(), withdrawLogPageDto.getSize());
        LambdaQueryWrapper<WithdrawLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.orderByDesc(WithdrawLog::getCreateTime);
        logLambdaQueryWrapper.eq(WithdrawLog::getUserId, player.getId());
        page = withdrawLogService.page(page, logLambdaQueryWrapper);
        List<WithdrawLogDto> withdrawLogDtoList = CopyUtil.copyList(page.getRecords(), WithdrawLogDto.class);
        withdrawLogPageDto.setList(withdrawLogDtoList);
        withdrawLogPageDto.setTotal((int) page.getTotal());
        responseDto.setContent(withdrawLogPageDto);
        return responseDto;
    }
}
