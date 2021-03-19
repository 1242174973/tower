package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-16
 */
@RestController
@RequestMapping("/account")
@Api(value = "账号请求", tags = "账号相关请求")
public class AccountController {

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private PlayerService playerService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "需要参数 昵称 图片验证码  图片验证码token  用户名  密码  推广码")
    public ResponseDto<PlayerDto> register(@ApiParam(value = "注册信息", required = true)
                                           @RequestBody PlayerDto playerDto) {
        BusinessUtil.require(playerDto.getAccount(), BusinessExceptionCode.ACCOUNT);
        BusinessUtil.length(playerDto.getAccount(), BusinessExceptionCode.ACCOUNT, 6, 20);
        BusinessUtil.require(playerDto.getNickName(), BusinessExceptionCode.NICK_NAME);
        BusinessUtil.length(playerDto.getNickName(), BusinessExceptionCode.NICK_NAME, 6, 20);
        BusinessUtil.require(playerDto.getPicCode(), BusinessExceptionCode.PIC_CODE);
        BusinessUtil.length(playerDto.getPicCode(), BusinessExceptionCode.PIC_CODE, 4);
        BusinessUtil.require(playerDto.getCodeToken(), BusinessExceptionCode.CODE_TOKEN);
        BusinessUtil.length(playerDto.getCodeToken(), BusinessExceptionCode.CODE_TOKEN, 6, 20);
        BusinessUtil.require(playerDto.getPassword(), BusinessExceptionCode.PASSWORD);
        BusinessUtil.length(playerDto.getPassword(), BusinessExceptionCode.PASSWORD, 6, 20);
        BusinessUtil.require(playerDto.getSpread(), BusinessExceptionCode.SPREAD);
        BusinessUtil.length(playerDto.getSpread(), BusinessExceptionCode.SPREAD, 4);
        BusinessUtil.assertParam(
                playerDto.getPicCode().equals(redisOperator.get(playerDto.getCodeToken())),
                "验证码错误");
        LambdaQueryWrapper<Player> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Player::getAccount, playerDto.getAccount());
        Player player = playerService.getOne(queryWrapper);
        BusinessUtil.assertParam(player == null, "用户已存在");
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getSpread, playerDto.getSpread());
        player = playerService.getOne(lambdaQueryWrapper);
        BusinessUtil.assertParam(player != null, "推广码不存在");
        int superId = player.getId();
        player = CopyUtil.copy(playerDto, Player.class);
        player.setCreateTime(LocalDateTime.now());
        player.setMoney(BigDecimal.ZERO);
        player.setSafeBox(BigDecimal.ZERO);
        player.setSpread(UuidUtil.getShortUuid(4));
        player.setSuperId(superId);
        player.setPic("http://www.baidu.com");
        player.setSalt(UuidUtil.getShortUuid(8));
        player.setPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(player.getPassword() + player.getSalt())));
        return getPlayerDtoResponseDto(player);
    }

    @PostMapping("/login/{account}/{password}")
    @ApiOperation(value = "用户登录", notes = "需要参数 账号  密码 ")
    public ResponseDto<PlayerDto> login(@ApiParam(value = "账号", required = true)
                                        @PathVariable String account,
                                        @ApiParam(value = "密码", required = true)
                                        @PathVariable String password) {
        BusinessUtil.require(account, BusinessExceptionCode.ACCOUNT);
        BusinessUtil.length(account, BusinessExceptionCode.ACCOUNT, 6, 20);
        BusinessUtil.require(password, BusinessExceptionCode.PASSWORD);
        BusinessUtil.length(password, BusinessExceptionCode.PASSWORD, 6, 20);
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getAccount, account);
        Player player = playerService.getOne(lambdaQueryWrapper);
        BusinessUtil.assertParam(player != null, "用户不存在");
        BusinessUtil.assertParam(
                player.getPassword().equals(MD5Utils.getMD5Str(MD5Utils.getMD5Str(password + player.getSalt()))),
                "密码不正确");
        return getPlayerDtoResponseDto(player);
    }

    @PostMapping("/login/{token}")
    @ApiOperation(value = "快速登录", notes = "需要参数 token ")
    public ResponseDto<PlayerDto> login(@ApiParam(value = "密码", required = true)
                                        @PathVariable String token) {
        BusinessUtil.require(token, BusinessExceptionCode.TOKEN);
        BusinessUtil.length(token, BusinessExceptionCode.TOKEN, 8);
        // 这边拿到的 token  前往redis获得用户信息返回
        Player player = JsonUtils.jsonToPojo(redisOperator.hget(RedisVariable.USER_INFO, token), Player.class);
        BusinessUtil.assertParam(player != null, "Token 已失效，请重新登录");
        return getPlayerDtoResponseDto(player);
    }

    @GetMapping("/randomName")
    @ApiOperation(value = "随机昵称", notes = "无需参数")
    public ResponseDto<String> randomName() {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setContent(NameRandomUtil.getRandomName());
        return responseDto;
    }

    @GetMapping("/checkName/{nickName}")
    @ApiOperation(value = "检查昵称", notes = "参数 昵称")
    public ResponseDto<String> checkName(@PathVariable String nickName) {
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getNickName, nickName);
        int playerCount = playerService.count(lambdaQueryWrapper);
        ResponseDto<String> responseDto = new ResponseDto<>();
        if (playerCount > 0) {
            responseDto.setSuccess(false);
            responseDto.setMessage("昵称已经存在");
        }
        return responseDto;
    }

    /**
     * 根据player对象获得返回体
     *
     * @param player 玩家
     * @return 返回体
     */
    public static ResponseDto<PlayerDto> getPlayerDtoResponseDto(Player player) {
        RedisOperator redisOperator=MyApplicationContextUti.getBean(RedisOperator.class);
        PlayerService playerService=MyApplicationContextUti.getBean(PlayerService.class);
        PlayerDto userDto = CopyUtil.copy(player, PlayerDto.class);
        String token = getToken(player.getId());
        redisOperator.hset(RedisVariable.USER_INFO, token, JsonUtils.objectToJson(player));
        userDto.setToken(token);
        playerService.saveOrUpdate(player);
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
        RedisOperator redisOperator=MyApplicationContextUti.getBean(RedisOperator.class);
        String token = redisOperator.hget(RedisVariable.USER_TOKEN, userId);
        if (token != null) {
            return token;
        }
        token = UuidUtil.getShortUuid();
        redisOperator.hset(RedisVariable.USER_TOKEN, userId, token);
        return token;
    }
}

