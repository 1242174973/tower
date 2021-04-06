package com.tower.controller.feign;

import com.tower.core.game.TowerGame;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.Player;
import com.tower.json.AttackInfo;
import com.tower.json.DataLog;
import com.tower.json.StartInfo;
import com.tower.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/27 11:42
 */
@RestController
@RequestMapping("/feign/snatch")
@Api(value = "数据请求", tags = "数据相关请求", hidden = true)
@Slf4j
public class SnatchFeignController {

    @Resource
    private TowerGame towerGame;

    @PostMapping("/data")
    @ApiOperation(value = "接收发送的数据", notes = "参数 数据")
    public void dataLog(@RequestBody String dataStr) {
        dataStr = dataStr.substring(1, dataStr.length() - 1).replace("\\\"", "\"");
        JsonUtils.jsonToPojo(dataStr, DataLog.class);
    }

    @PostMapping("/start")
    @ApiOperation(value = "接收开始请求", notes = "参数 数据")
    public void start(@RequestBody String startStr) {
        log.info("收到开始请求");
        startStr = startStr.substring(1, startStr.length() - 1).replace("\\\"", "\"");
        StartInfo startInfo = JsonUtils.jsonToPojo(startStr, StartInfo.class);
        if (startInfo.getStartTime() != towerGame.getStartTime() && startInfo.getStartTime() >= towerGame.getEndTime() && towerGame.isAward()) {
            towerGame.setStartTime(startInfo.getStartTime());
            towerGame.setAwardTime(startInfo.getAwardTime());
            towerGame.setEndTime(startInfo.getEndTime());
            towerGame.setMonsterId(null);
            towerGame.setVer(startInfo.getVer());
            towerGame.gameStart();
            log.info("游戏开始成功");
        }
    }

    @PostMapping("/attack")
    @ApiOperation(value = "接收出怪数据", notes = "参数 数据")
    public void attack(@RequestBody String attackStr) {
        log.info("收到出怪请求");
        attackStr = attackStr.substring(1, attackStr.length() - 1).replace("\\\"", "\"");
        AttackInfo attackInfo = JsonUtils.jsonToPojo(attackStr, AttackInfo.class);
        if (towerGame.getMonsterId() != null) {
            return;
        }
        switch (attackInfo.getMonsterId()) {
            case 4:
                attackInfo.setMonsterId(5);
                break;
            case 5:
                attackInfo.setMonsterId(7);
                break;
            case 6:
                attackInfo.setMonsterId(8);
                break;
            case 7:
                attackInfo.setMonsterId(4);
                break;
            case 8:
                attackInfo.setMonsterId(6);
                break;
            default:
                break;
        }
        if (System.currentTimeMillis() > towerGame.getAwardTime() &&
                System.currentTimeMillis() < towerGame.getEndTime()) {
            towerGame.setMonsterId(attackInfo.getMonsterId());
            towerGame.insertAttackLog();
            log.info("设置出怪成功");
        }
    }
}
