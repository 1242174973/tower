package com.tower.controller;

import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.TransferLogDto;
import com.tower.entity.Player;
import com.tower.entity.TransferLog;
import com.tower.exception.BusinessException;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.PlayerService;
import com.tower.service.TransferLogService;
import com.tower.utils.BusinessUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/24 13:44
 */
@RestController
@RequestMapping("/transfer")
@Api(value = "转账信息", tags = "转账信息相关请求")
public class TransferController {

    @Resource
    private PlayerService playerService;

    @Resource
    private TransferLogService transferLogService;

    @PostMapping("/transfer")
    @ApiOperation(value = "转账请求", notes = "参数 接收人ID 转账金额")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> transfer(Player player, @RequestBody TransferLogDto transferLogDto) {
        BusinessUtil.require(transferLogDto.getReceptionId(), BusinessExceptionCode.RECEPTION_ID);
        BusinessUtil.require(transferLogDto.getCoin(), BusinessExceptionCode.COIN);
        BusinessUtil.assertParam(transferLogDto.getCoin().doubleValue() > 0, "转账分数必须大于0");
        Player receptionPlayer = playerService.getById(transferLogDto.getReceptionId());
        TransferLog transferLog = new TransferLog();
        transferLog.setTransferId(player.getId());
        transferLog.setReceptionId(transferLogDto.getReceptionId());
        transferLog.setCoin(transferLogDto.getCoin());
        transferLog.setCreateTime(LocalDateTime.now());
        try {
            transferLog.setSuccess(1);
            BusinessUtil.assertParam(receptionPlayer != null, "接收玩家找不到");
            BusinessUtil.assertParam(player.getMoney().doubleValue() > transferLogDto.getCoin().doubleValue(), "玩家分数不足");
            player.setMoney(player.getMoney().subtract(transferLogDto.getCoin()));
            receptionPlayer.setMoney(receptionPlayer.getMoney().add(transferLogDto.getCoin()));
            transferLogService.save(transferLog);
        } catch (BusinessException ex) {
            transferLog.setSuccess(0);
            transferLogService.save(transferLog);
            throw ex;
        }
        AccountController.getPlayerDtoResponseDto(receptionPlayer);
        return AccountController.getPlayerDtoResponseDto(player);
    }
}
