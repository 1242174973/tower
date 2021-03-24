package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.TopUpConfigDto;
import com.tower.dto.TopUpLogDto;
import com.tower.entity.Player;
import com.tower.entity.TopUpConfig;
import com.tower.entity.TopUpLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.TopUpConfigService;
import com.tower.service.TopUpLogService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-17
 */
@RestController
@RequestMapping("/topUp")
@Api(value = "充值信息", tags = "充值信息相关请求")
public class TopUpController {

    @Resource
    private TopUpConfigService topUpConfigService;

    @Resource
    private TopUpLogService topUpLogService;

    @GetMapping("/topUpList")
    @ApiOperation(value = "获取所有充值信息", notes = "无需参数")
    public ResponseDto<List<TopUpConfigDto>> topUpList() {
        ResponseDto<List<TopUpConfigDto>> responseDto = new ResponseDto<>();
        LambdaQueryWrapper<TopUpConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<TopUpConfig> topUpConfigs = topUpConfigService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<TopUpConfigDto> topUpConfigDtoList = CopyUtil.copyList(topUpConfigs, TopUpConfigDto.class);
        responseDto.setContent(topUpConfigDtoList);
        return responseDto;
    }

    @PostMapping("/topUp")
    @ApiOperation(value = "充值信息", notes = "参数 充值信息ID 汇款人 充值金额")
    public ResponseDto<PlayerDto> topUp(Player player, @RequestBody TopUpLogDto topUpLogDto) {
        BusinessUtil.assertParam(topUpLogDto.getPayee() != null, "汇款人不能为空");
        BusinessUtil.require(topUpLogDto.getTopUpMoney(), BusinessExceptionCode.TOP_UP_MONEY);
        BusinessUtil.require(topUpLogDto.getTopUpId(), BusinessExceptionCode.TOP_UP_ID);
        TopUpConfig topUpConfig = topUpConfigService.getById(topUpLogDto.getTopUpId());
        BusinessUtil.assertParam(topUpConfig != null, "找不到对应的操作信息");
        TopUpLog topUpLog = new TopUpLog()
                .setTopUpMoney(topUpLogDto.getTopUpMoney())
                .setBankCardName(topUpConfig.getBankCardName())
                .setBankCardNum(topUpConfig.getBankCardNum())
                .setUserId(player.getId())
                .setState(0)
                .setOrder(DateUtils.getNowDate() + (new Random().nextInt(900000) + 100000))
                .setCreateTime(LocalDateTime.now());
        topUpLogService.save(topUpLog);
        return AccountController.getPlayerDtoResponseDto(player);
    }

}

