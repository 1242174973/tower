package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.SignInDto;
import com.tower.entity.Player;
import com.tower.entity.SignIn;
import com.tower.exception.BusinessExceptionCode;
import com.tower.utils.BusinessUtil;
import com.tower.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/18 17:45
 */
@RestController
@RequestMapping("/safeBox")
@Api(value = "保险柜", tags = "保险柜信息相关请求")
public class SafeBoxController {

    @GetMapping("/withdraw /{coin}")
    @ApiOperation(value = "保险柜存取分", notes = "参数 分数  大于0取反小于0存分")
    public ResponseDto<PlayerDto> monsterList(Player player,
                                              @ApiParam(value = "分数", required = true)
                                              @PathVariable int coin) {
        ResponseDto<PlayerDto> responseDto = new ResponseDto<>();
        //取分操作
        if (coin > 0) {
            BusinessUtil.assertParam(player.getSafeBox().doubleValue() > coin, "保险柜分数不够，取反失败");
            player.setMoney(player.getMoney().add(BigDecimal.valueOf(coin)));
            player.setSafeBox(player.getMoney().subtract(BigDecimal.valueOf(coin)));
        } else {
            BusinessUtil.assertParam(player.getMoney().doubleValue() > coin, "玩家分数不够，存分失败");
            player.setMoney(player.getMoney().subtract(BigDecimal.valueOf(coin)));
            player.setSafeBox(player.getMoney().add(BigDecimal.valueOf(coin)));
        }
        return AccountController.getPlayerDtoResponseDto(player);
    }

}
