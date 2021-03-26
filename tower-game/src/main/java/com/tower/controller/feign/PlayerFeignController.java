package com.tower.controller.feign;

import com.tower.controller.AccountController;
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
@Api(value = "玩家请求", tags = "玩家相关请求")
public class PlayerFeignController {
    @PostMapping("/save")
    @ApiOperation(value = "保存玩家信息", notes = "参数 玩家信息对象")
    public void save(@RequestBody Player player) {
        AccountController.getPlayerDtoResponseDto(player);
    }

}
