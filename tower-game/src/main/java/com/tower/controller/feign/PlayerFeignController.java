package com.tower.controller.feign;

import com.tower.TowerApplication;
import com.tower.controller.AccountController;
import com.tower.core.utils.PlayerUtils;
import com.tower.entity.Player;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/26 16:35
 */
@RestController
@RequestMapping("/feign/player")
@Api(value = "玩家请求", tags = "玩家相关请求", hidden = true)
public class PlayerFeignController {
    @PostMapping("/save")
    @ApiOperation(value = "保存玩家信息", notes = "参数 玩家信息对象")
    public void save(@RequestBody Player player) {
        PlayerUtils.savePlayer(player);
    }

    @GetMapping("/playerInfo/{id}")
    @ApiOperation(value = "获得玩家信息", notes = "参数 玩家ID")
    public Player playerInfo(@PathVariable int id) {
        return PlayerUtils.getPlayer(id);
    }

    @GetMapping("/getStatus")
    @ApiOperation(value = "获得游戏状态", notes = "无需参数")
    public int getStatus() {
        return TowerApplication.status;
    }

    @GetMapping("/setStatus/{status}")
    @ApiOperation(value = "获得玩家信息", notes = "参数 玩家ID")
    public void setStatus(@PathVariable int status) {
        TowerApplication.status = status;
    }
}
