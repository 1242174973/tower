package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.PlayerPageDto;
import com.tower.entity.Player;
import com.tower.entity.SignIn;
import com.tower.exception.BusinessExceptionCode;
import com.tower.feign.PlayerFeign;
import com.tower.service.PlayerService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/22 9:46
 */
@Api(value = "玩家", tags = "玩家相关请求")
@RequestMapping("/player")
@RestController
public class PlayerController {

    @Resource
    private PlayerService playerService;

    @Resource
    private PlayerFeign playerFeign;

    @PostMapping("/list")
    @ApiOperation(value = "获得所有玩家", notes = "获得所有玩家请求")
    public ResponseDto<PlayerPageDto> list(@RequestBody PlayerPageDto pageDto) {
        BusinessUtil.assertParam(pageDto.getPage() > 0, "页数必须大于0");
        BusinessUtil.assertParam(pageDto.getSize() > 0, "条数必须大于0");
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(pageDto.getSearch())) {
            lambdaQueryWrapper
                    .or(queryWrapper -> queryWrapper.like(Player::getAccount, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Player::getNickName, pageDto.getSearch()))
                    .or(queryWrapper -> queryWrapper.like(Player::getId, pageDto.getSearch()));
        }
        lambdaQueryWrapper.orderByDesc(Player::getCreateTime);
        Page<Player> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = playerService.page(page, lambdaQueryWrapper);
        List<PlayerDto> playerDtoList = CopyUtil.copyList(page.getRecords(), PlayerDto.class);
        pageDto.setList(playerDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<PlayerPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }


    @PostMapping("/editCoin/{playerId}/{addMoney}")
    @ApiOperation(value = "修改玩家密码", notes = "修改玩家请求")
    public ResponseDto<String> editCoin(@ApiParam(value = "玩家ID", required = true)
                                        @PathVariable int playerId,
                                        @PathVariable int addMoney) {
        Player player = playerService.getById(playerId);
        BusinessUtil.assertParam(player != null, "玩家没找到");
        player.setMoney(player.getMoney().add(BigDecimal.valueOf(addMoney)));
        playerFeign.save(player);
        return new ResponseDto<>();
    }

    @PostMapping("/editPassword/{playerId}/{password}")
    @ApiOperation(value = "修改玩家密码", notes = "修改玩家请求")
    public ResponseDto<String> v(@ApiParam(value = "玩家ID", required = true)
                                 @PathVariable int playerId,
                                 @PathVariable String password) {
        Player player = playerService.getById(playerId);
        BusinessUtil.assertParam(player != null, "玩家没找到");
        player.setPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(password + player.getSalt())));
        playerFeign.save(player);
        return new ResponseDto<>();
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改玩家", notes = "修改玩家请求")
    public ResponseDto<String> edit(@ApiParam(value = "玩家ID", required = true)
                                    @RequestBody PlayerDto playerDto) {
        BusinessUtil.require(playerDto.getId(), BusinessExceptionCode.ID);
        BusinessUtil.require(playerDto.getVip(), BusinessExceptionCode.VIP_LEVEL);
        BusinessUtil.require(playerDto.getExperience(), BusinessExceptionCode.EXPERIENCE);
        BusinessUtil.require(playerDto.getMoney(), BusinessExceptionCode.MONEY);
        BusinessUtil.require(playerDto.getSafeBox(), BusinessExceptionCode.SAFE_BOX);
        BusinessUtil.require(playerDto.getSignIn(), BusinessExceptionCode.SIGN_IN);
        BusinessUtil.require(playerDto.getTotalSignIn(), BusinessExceptionCode.TOTAL_SIGN_IN);
        Player player = playerService.getById(playerDto.getId());
        BusinessUtil.assertParam(player != null, "玩家没找到");
        player.setVip(playerDto.getVip());
        player.setExperience(playerDto.getExperience());
        player.setMoney(playerDto.getMoney());
        player.setSafeBox(playerDto.getSafeBox());
        player.setSignIn(playerDto.getSignIn());
        player.setTotalSignIn(playerDto.getTotalSignIn());
        playerFeign.save(player);
        return new ResponseDto<>();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除玩家", notes = "删除玩家请求")
    public ResponseDto<String> delete(@ApiParam(value = "玩家ID", required = true)
                                      @PathVariable int id) {
        playerService.removeById(id);
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent("删除成功");
        return responseDto;
    }
}
