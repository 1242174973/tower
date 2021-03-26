package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.TransferLogDto;
import com.tower.dto.page.game.TransferLogPageDto;
import com.tower.entity.Player;
import com.tower.entity.TransferLog;
import com.tower.exception.BusinessException;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.PlayerService;
import com.tower.service.TransferLogService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
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
        PlayerUtils.savePlayer(receptionPlayer);
        return AccountController.getPlayerDtoResponseDto(player);
    }

    @PostMapping("/transferLog")
    @ApiOperation(value = "获取转账记录", notes = "参数 分页参数")
    public ResponseDto<TransferLogPageDto> topUpLogList(Player player, @RequestBody TransferLogPageDto transferLogPageDto) {
        BusinessUtil.assertParam(transferLogPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(transferLogPageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<TransferLogPageDto> responseDto = new ResponseDto<>();
        Page<TransferLog> page = new Page<>(transferLogPageDto.getPage(), transferLogPageDto.getSize());
        LambdaQueryWrapper<TransferLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .or(wrapper -> wrapper.eq(TransferLog::getReceptionId, player.getId()))
                .or(wrapper -> wrapper.eq(TransferLog::getTransferId, player.getId()));
        page = transferLogService.page(page, lambdaQueryWrapper);
        transferLogPageDto.setTotal((int) page.getTotal());
        List<TransferLogDto> topUpLogDtoList = CopyUtil.copyList(page.getRecords(), TransferLogDto.class);
        transferLogPageDto.setList(topUpLogDtoList);
        responseDto.setContent(transferLogPageDto);
        return responseDto;
    }
}
