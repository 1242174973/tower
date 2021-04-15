package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.SafeBoxLogDto;
import com.tower.dto.page.SafeBoxLogPageDto;
import com.tower.entity.Player;
import com.tower.entity.SafeBoxLog;
import com.tower.service.SafeBoxLogService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
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

/**
 * @author 梦-屿-千-寻
 * @date2021/3/18 17:45
 */
@RestController
@RequestMapping("/safeBox")
@Api(value = "保险柜", tags = "保险柜信息相关请求")
public class SafeBoxController {

    @Resource
    private SafeBoxLogService safeBoxLogService;

    @GetMapping("/withdraw/{coin}")
    @ApiOperation(value = "保险柜存取分", notes = "参数 分数  大于0存分 小于0取分")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> withdraw(Player player,
                                           @ApiParam(value = "分数", required = true)
                                           @PathVariable int coin) {
        BusinessUtil.assertParam(coin != 0, "操作分数不能为零");
        SafeBoxLog safeBoxLog = new SafeBoxLog();
        safeBoxLog.setUserId(player.getId());
        safeBoxLog.setWithdraw(coin);
        safeBoxLog.setCreateTime(LocalDateTime.now());
        //取分操作
        if (coin > 0) {
            BusinessUtil.assertParam(player.getMoney().doubleValue() >= coin, "玩家分数不够，存分失败");
            player.setMoney(player.getMoney().subtract(BigDecimal.valueOf(coin)));
            player.setSafeBox(player.getSafeBox().add(BigDecimal.valueOf(coin)));
        } else {
            coin=-coin;
            BusinessUtil.assertParam(player.getSafeBox().doubleValue() >= coin, "保险柜分数不够，取反失败");
            player.setMoney(player.getMoney().add(BigDecimal.valueOf(coin)));
            player.setSafeBox(player.getSafeBox().subtract(BigDecimal.valueOf(coin)));
        }
        safeBoxLogService.save(safeBoxLog);
        return PlayerUtils.getPlayerDtoResponseDto(player);
    }

    @PostMapping("/withdrawList")
    @ApiOperation(value = "保险柜存取分记录", notes = "参数 分页参数")
    public ResponseDto<SafeBoxLogPageDto> withdrawList(Player player,
                                                       @ApiParam(value = "获取信息", required = true)
                                                       @RequestBody SafeBoxLogPageDto safeBoxLogPageDto) {
        BusinessUtil.assertParam(safeBoxLogPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(safeBoxLogPageDto.getSize() > 0, "条数必须大于0");
        ResponseDto<SafeBoxLogPageDto> responseDto = new ResponseDto<>();
        Page<SafeBoxLog> page=new Page<>(safeBoxLogPageDto.getPage(),safeBoxLogPageDto.getSize());
        LambdaQueryWrapper<SafeBoxLog> logLambdaQueryWrapper=new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.orderByDesc(SafeBoxLog::getCreateTime);
        logLambdaQueryWrapper.eq(SafeBoxLog::getUserId,player.getId());
        page=safeBoxLogService.page(page,logLambdaQueryWrapper);
        List<SafeBoxLogDto> playerDtoList = CopyUtil.copyList(page.getRecords(), SafeBoxLogDto.class);
        safeBoxLogPageDto.setList(playerDtoList);
        safeBoxLogPageDto.setTotal((int) page.getTotal());
        responseDto.setContent(safeBoxLogPageDto);
        return responseDto;
    }
}
