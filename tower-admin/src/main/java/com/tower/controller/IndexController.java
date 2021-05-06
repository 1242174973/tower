package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.IndexDto;
import com.tower.dto.MessageDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.Player;
import com.tower.entity.TopUpLog;
import com.tower.entity.WithdrawLog;
import com.tower.feign.PlayerFeign;
import com.tower.service.PlayerService;
import com.tower.service.TopUpLogService;
import com.tower.service.WithdrawLogService;
import com.tower.utils.DateUtils;
import com.tower.utils.JsonUtils;
import com.tower.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xxxx
 * @date 2021/4/8 15:43
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页相关请求", tags = "首页相关请求")
public class IndexController {

    @Resource
    private PlayerService playerService;


    @Resource
    private TopUpLogService topUpLogService;

    @Resource
    private WithdrawLogService withdrawLogService;


    @Resource
    private RedisOperator redisOperator;

    @Resource
    private PlayerFeign feign;

    @PostMapping("/index")
    @ApiOperation(value = "获取首页信息", notes = "获取首页信息")
    public ResponseDto<IndexDto> list() {
        IndexDto indexDto = new IndexDto();
        List<Player> playerList = playerService.getBaseMapper().selectList(new LambdaQueryWrapper<>());
        indexDto.setNewNum(getNewNum(playerList));
        indexDto.setActive(getActiveNum(playerList));
        indexDto.setNotActive(playerList.size() - indexDto.getActive());
        indexDto.setStatus(feign.getStatus());
        ResponseDto<IndexDto> responseDto = new ResponseDto<>();
        responseDto.setContent(indexDto);
        return responseDto;
    }

    @PostMapping("/stop")
    @ApiOperation(value = "一键维护", notes = "一键维护")
    public ResponseDto<String> stop() {
        feign.setStatus(1);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setMessage("维护成功");
        return responseDto;
    }

    @PostMapping("/start")
    @ApiOperation(value = "一键开启", notes = "一键开启")
    public ResponseDto<String> start() {
        feign.setStatus(0);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setMessage("开启成功");
        return responseDto;
    }

    @GetMapping("/getMessage")
    @ApiOperation(value = "获取充值数据", notes = "获取充值数据")
    public ResponseDto<MessageDto> getMessage() {
        String messageDtoStr = redisOperator.get("MESSAGE_DTO");
        MessageDto messageDto;
        if (messageDtoStr != null) {
            messageDto = JsonUtils.jsonToPojo(messageDtoStr, MessageDto.class);
        } else {
            messageDto = new MessageDto();
            messageDto.setTopUp(topUpLogService.count(new LambdaQueryWrapper<TopUpLog>().eq(TopUpLog::getState, 0)));
            messageDto.setWithdraw(withdrawLogService.count(new LambdaQueryWrapper<WithdrawLog>().eq(WithdrawLog::getState, 0)));
            redisOperator.set("MESSAGE_DTO", JsonUtils.objectToJson(messageDto), 1);
        }
        ResponseDto<MessageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(messageDto);
        return responseDto;
    }

    @PostMapping("/removeAll")
    @ApiOperation(value = "一键清除数据", notes = "一键清除数据")
    public ResponseDto<String> removeAll() {
        feign.removeAll("qwe123456");
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setMessage("清除成功");
        return responseDto;
    }

    /**
     * 查询活跃的玩家
     *
     * @param playerList 玩家数组
     * @return 活跃人数
     */
    private int getActiveNum(List<Player> playerList) {
        if (playerList.size() <= 0) {
            return 0;
        }
        AtomicInteger num = new AtomicInteger();
        playerList.forEach(player -> {
            String zone = "+8";
            long time = DateUtils.byDayLocalDateTime(0).toInstant(ZoneOffset.of(zone)).toEpochMilli();
            if (player.getSignInTime().toInstant(ZoneOffset.of(zone)).toEpochMilli() > time ||
                    player.getCreateTime().toInstant(ZoneOffset.of(zone)).toEpochMilli() > time) {
                num.getAndIncrement();
            }
        });
        return num.get();
    }

    /**
     * 查询新增的玩家
     *
     * @param playerList 玩家数组
     * @return 新增的玩家
     */
    private int getNewNum(List<Player> playerList) {
        if (playerList.size() <= 0) {
            return 0;
        }
        AtomicInteger num = new AtomicInteger();
        playerList.forEach(player -> {
            long time = DateUtils.byDayLocalDateTime(0).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (player.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() > time) {
                num.getAndIncrement();
            }
        });
        return num.get();
    }

}
