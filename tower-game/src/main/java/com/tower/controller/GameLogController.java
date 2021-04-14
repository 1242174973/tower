package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.AgentDto;
import com.tower.dto.GameLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.game.GameLogPageDto;
import com.tower.dto.page.game.WelFareLogPageDto;
import com.tower.entity.GameLog;
import com.tower.entity.Player;
import com.tower.service.GameLogService;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import com.tower.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/6 18:02
 */
@RestController
@RequestMapping("/gameLog")
@Api(value = "游戏记录相关请求", tags = "游戏记录相关请求")
public class GameLogController {

    @Resource
    private GameLogService gameLogService;

    @PostMapping("/gameLogList")
    @ApiOperation(value = "游戏记录", notes = "参数 分页参数")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<GameLogPageDto> agentIndex(Player player, @RequestBody GameLogPageDto gameLogPageDto) {
        BusinessUtil.assertParam(gameLogPageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(gameLogPageDto.getSize() > 0, "条数必须大于0");
        Page<GameLog> page = new Page<>(gameLogPageDto.getPage(), gameLogPageDto.getSize());
        String startTime;
        String endTime;
        //查询最近3天的数据
        if (gameLogPageDto.getRecentDay() <= 0) {
            startTime = DateUtils.getDate(-1);
            endTime = DateUtils.getDate(1);
        }
        //查询N天前的数据
        else {
            startTime = DateUtils.getDate(-gameLogPageDto.getRecentDay()+1);
            endTime = DateUtils.getDate(-gameLogPageDto.getRecentDay() + 2);
        }
        System.out.println(startTime);
        System.out.println(endTime);
        LambdaQueryWrapper<GameLog> logLambdaQueryWrapper=new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(GameLog::getUserId,player.getId())
                .ge(GameLog::getCreateTime, startTime)
                .le(GameLog::getCreateTime, endTime);
        page = gameLogService.page(page, logLambdaQueryWrapper);
        List<GameLogDto> gameLogDtoList = CopyUtil.copyList(page.getRecords(), GameLogDto.class);
        gameLogPageDto.setList(gameLogDtoList);
        gameLogPageDto.setTotal((int) page.getTotal());
        ResponseDto<GameLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(gameLogPageDto);
        return responseDto;
    }
}
