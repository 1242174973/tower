package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.GameLogDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.GameLogPageDto;
import com.tower.entity.GameLog;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.GameLogService;
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
@RequestMapping("/gameLog")
@Api(value = "游戏记录", tags = "游戏记录相关请求")
public class GameLogController {

    @Resource
    private GameLogService gameLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有游戏记录", notes = "获得所有游戏记录请求")
    public ResponseDto<GameLogPageDto> list(@RequestBody GameLogPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<GameLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(GameLog::getId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(GameLog::getOrderId, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(GameLog::getUserId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(GameLog::getCreateTime);
        Page<GameLog> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = gameLogService.page(page, lambdaQueryWrapper);
        List<GameLogDto> gameLogDtoList = CopyUtil.copyList(page.getRecords(), GameLogDto.class);
        pageDto.setList(gameLogDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<GameLogPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加游戏记录", notes = "添加游戏记录请求")
    public ResponseDto<GameLogDto> add(@ApiParam(value = "游戏记录信息", required = true)
                                       @RequestBody GameLogDto gameLogDto) {
        requireParam(gameLogDto);
        GameLog gameLog = CopyUtil.copy(gameLogDto, GameLog.class);
        gameLog.setCreateTime(LocalDateTime.now());
        gameLogService.save(gameLog);
        ResponseDto<GameLogDto> responseDto = new ResponseDto<>();
        gameLogDto = CopyUtil.copy(gameLog, GameLogDto.class);
        responseDto.setContent(gameLogDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改游戏记录", notes = "修改游戏记录请求")
    public ResponseDto<GameLogDto> edit(@ApiParam(value = "游戏记录信息", required = true)
                                        @RequestBody GameLogDto gameLogDto) {
        requireParam(gameLogDto);
        BusinessUtil.require(gameLogDto.getId(), BusinessExceptionCode.ID);
        GameLog gameLog = gameLogService.getById(gameLogDto.getId());
        BusinessUtil.assertParam(gameLog != null, "游戏记录没找到");
        gameLogService.saveOrUpdate(gameLog);
        ResponseDto<GameLogDto> responseDto = new ResponseDto<>();
        gameLogDto = CopyUtil.copy(gameLog, GameLogDto.class);
        responseDto.setContent(gameLogDto);
        return responseDto;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除游戏记录", notes = "删除游戏记录请求")
    public ResponseDto<String> delete(@ApiParam(value = "游戏记录ID", required = true)
                                      @PathVariable int id) {
        gameLogService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }


    /**
     * 校验参数
     *
     * @param gameLogDto 参数
     */
    private void requireParam(GameLogDto gameLogDto) {

    }
}
