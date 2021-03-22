package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.page.PlayerPageDto;
import com.tower.entity.Player;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.PlayerService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        Page<Player> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        page = playerService.page(page, lambdaQueryWrapper);
        List<PlayerDto> playerDtoList = CopyUtil.copyList(page.getRecords(), PlayerDto.class);
        pageDto.setList(playerDtoList);
        pageDto.setTotal((int) page.getTotal());
        ResponseDto<PlayerPageDto> responseDto = new ResponseDto<>();
        responseDto.setContent(pageDto);
        return responseDto;
    }


    @PostMapping("/edit")
    @ApiOperation(value = "修改玩家", notes = "修改玩家请求")
    public ResponseDto<PlayerDto> edit(@ApiParam(value = "玩家ID", required = true)
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
        player.setExperience(player.getExperience());
        player.setMoney(player.getMoney());
        player.setSafeBox(player.getSafeBox());
        player.setSignIn(player.getSignIn());
        player.setTotalSignIn(player.getTotalSignIn());
        return getPlayerDtoResponseDto(player);
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

    /**
     * 根据player对象获得返回体
     *
     * @param player 玩家
     * @return 返回体
     */
    public static ResponseDto<PlayerDto> getPlayerDtoResponseDto(Player player) {
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        PlayerService playerService = MyApplicationContextUti.getBean(PlayerService.class);
        playerService.saveOrUpdate(player);
        PlayerDto userDto = CopyUtil.copy(player, PlayerDto.class);
        String token = getToken(player.getId());
        redisOperator.hset(RedisVariable.USER_INFO, token, JsonUtils.objectToJson(player));
        userDto.setToken(token);
        ResponseDto<PlayerDto> responseDto = new ResponseDto<>();
        responseDto.setContent(userDto);
        return responseDto;
    }

    /**
     * 获得token
     *
     * @param userId userId
     * @return token
     */
    private static String getToken(int userId) {
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        String token = redisOperator.hget(RedisVariable.USER_TOKEN, userId);
        if (token != null) {
            return token;
        }
        token = UuidUtil.getShortUuid();
        redisOperator.hset(RedisVariable.USER_TOKEN, userId, token);
        return token;
    }
}
