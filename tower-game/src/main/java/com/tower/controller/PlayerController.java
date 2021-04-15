package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.Player;
import com.tower.exception.BusinessExceptionCode;
import com.tower.service.PlayerService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/16 17:36
 */
@RestController
@RequestMapping("/player")
@Api(value = "用户请求", tags = "用户相关请求")
public class PlayerController {

    @Resource
    private PlayerService playerService;

    @ApiOperation(value = "设置新密码", notes = "根据旧密码设置新密码 参数 旧密码 新密码")
    @PostMapping("/setNewPassword/{oldPassword}/{newPassword}")
    public ResponseDto<PlayerDto> setNewPassword(Player player,
                                                 @ApiParam(value = "旧密码", required = true)
                                                 @PathVariable String oldPassword,
                                                 @ApiParam(value = "新密码", required = true)
                                                 @PathVariable String newPassword) {
        BusinessUtil.require(oldPassword, BusinessExceptionCode.OLD_PASSWORD);
        BusinessUtil.length(oldPassword, BusinessExceptionCode.OLD_PASSWORD, 6, 20);
        BusinessUtil.require(newPassword, BusinessExceptionCode.NEW_PASSWORD);
        BusinessUtil.length(newPassword, BusinessExceptionCode.NEW_PASSWORD, 6, 20);
        BusinessUtil.assertParam(
                MD5Utils.getMD5Str(MD5Utils.getMD5Str(oldPassword + player.getSalt())).equals(player.getPassword()),
                "旧密码不正确");
        player.setPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(newPassword + player.getSalt())));
        PlayerUtils.savePlayer(player);
        PlayerUtils.setNewToken(player);
        return PlayerUtils.getPlayerDtoResponseDtoNotSave(player);
    }

    @ApiOperation(value = "设置保险柜新密码", notes = "根据旧密码设置保险柜新密码 参数 旧密码 新密码")
    @PostMapping("/setSafeBoxPassword/{oldPassword}/{newPassword}")
    public ResponseDto<PlayerDto> setSafeBoxPassword(Player player,
                                                     @ApiParam(value = "旧密码(没有填空)", required = true)
                                                     @PathVariable String oldPassword,
                                                     @ApiParam(value = "新密码", required = true)
                                                     @PathVariable String newPassword) {
        BusinessUtil.require(oldPassword, BusinessExceptionCode.OLD_PASSWORD);
        BusinessUtil.length(oldPassword, BusinessExceptionCode.OLD_PASSWORD, 6, 20);
        BusinessUtil.require(newPassword, BusinessExceptionCode.NEW_PASSWORD);
        BusinessUtil.length(newPassword, BusinessExceptionCode.NEW_PASSWORD, 6, 20);
        if (!StringUtils.isEmpty(player.getSafeBoxPassword())) {
            BusinessUtil.assertParam(
                    MD5Utils.getMD5Str(MD5Utils.getMD5Str(oldPassword + player.getSalt())).equals(player.getSafeBoxPassword()),
                    "旧密码不正确");
        }
        player.setSafeBoxPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(newPassword + player.getSalt())));
        return PlayerUtils.getPlayerDtoResponseDto(player);
    }

    @ApiOperation(value = "设置新昵称", notes = "设置新昵称 参数 新昵称")
    @PostMapping("/setNickName/{nickName}")
    public ResponseDto<PlayerDto> setNickName(Player player,
                                              @ApiParam(value = "新昵称", required = true)
                                              @PathVariable String nickName) {
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getNickName, nickName);
        int playerCount = playerService.count(lambdaQueryWrapper);
        if (playerCount>0) {
            ResponseDto<PlayerDto> responseDto = new ResponseDto<>();
            responseDto.setSuccess(false);
            responseDto.setMessage("昵称已经存在");
            return responseDto;
        }
        BusinessUtil.require(nickName, BusinessExceptionCode.NICK_NAME);
        BusinessUtil.length(nickName, BusinessExceptionCode.NICK_NAME, 1, 20);
        player.setNickName(nickName);
        return PlayerUtils.getPlayerDtoResponseDto(player);
    }
}
