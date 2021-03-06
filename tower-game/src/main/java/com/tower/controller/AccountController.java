package com.tower.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.constant.Mid;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.MsgUtil;
import com.tower.core.utils.PlayerUtils;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.Player;
import com.tower.entity.UserWithdrawConfig;
import com.tower.exception.BusinessExceptionCode;
import com.tower.msg.Tower;
import com.tower.service.PlayerService;
import com.tower.service.UserWithdrawConfigService;
import com.tower.service.my.MyChallengeRewardService;
import com.tower.service.my.MySalvageService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.netty.channel.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xxxx
 * @since 2021-03-16
 */
@RestController
@RequestMapping("/account")
@Api(value = "账号请求", tags = "账号相关请求")
public class AccountController {

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private UserWithdrawConfigService userWithdrawConfigService;

    @Resource
    private PlayerService playerService;

    @Resource
    private MyChallengeRewardService challengeRewardService;

    @Resource
    private MySalvageService salvageService;


    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "需要参数 昵称 图片验证码  图片验证码token  用户名  密码  推广码")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseDto<PlayerDto> register(@ApiParam(value = "注册信息", required = true)
                                           @RequestBody PlayerDto playerDto) {
        BusinessUtil.require(playerDto.getAccount(), BusinessExceptionCode.ACCOUNT);
        BusinessUtil.length(playerDto.getAccount(), BusinessExceptionCode.ACCOUNT, 6, 20);
        BusinessUtil.require(playerDto.getNickName(), BusinessExceptionCode.NICK_NAME);
        BusinessUtil.length(playerDto.getNickName(), BusinessExceptionCode.NICK_NAME, 1, 20);
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
        queryWrapper.or(wrapper -> wrapper.eq(Player::getAccount, playerDto.getAccount()))
                .or(wrapper -> wrapper.eq(Player::getNickName, playerDto.getNickName()));
        Player player = playerService.getOne(queryWrapper);
        BusinessUtil.assertParam(player == null, "用户已存在");
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getSpread, playerDto.getSpread());
        player = getAgentPlayer(playerService.getOne(lambdaQueryWrapper));
        BusinessUtil.assertParam(player != null, "推广码不存在");
        int superId = player.getId();
        player = CopyUtil.copy(playerDto, Player.class)
                .setCreateTime(LocalDateTime.now())
                .setMoney(BigDecimal.ZERO)
                .setSafeBox(BigDecimal.ZERO)
                .setSpread(PlayerUtils.getShortUuid())
                .setSuperId(superId)
                .setIsAgent(0)
                .setSignIn(0)
                .setTotalSignIn(0)
                .setPic("https://img02.sogoucdn.com/v2/thumb/retype_exclude_gif/ext/auto/q/80/crop/xy/ai/w/160/h/160/resize/w/160?url=https%3A%2F%2Fimg02.sogoucdn.com%2Fapp%2Fa%2F10010016%2F4e2cfdceac8118da34011cb5c49da00b&appid=201003&sign=676de451cea1a4192b7eede671eae0ce")
                .setSalt(UuidUtil.getShortUuid(8))
                .setSignInTime(DateUtils.byDayLocalDateTime(-1))
                .setRebate(BigDecimal.ZERO)
                .setTax(BigDecimal.ZERO)
                .setVip(0)
                .setExperience(0)
                .setTotalAward(BigDecimal.ZERO)
                .setCanAward(BigDecimal.ZERO)
                .setPresent(0)
                .setPassword(MD5Utils.getMD5Str(MD5Utils.getMD5Str(player.getPassword() + player.getSalt())));
        ResponseDto<PlayerDto> playerDtoResponseDto = PlayerUtils.getPlayerDtoResponseDto(player);
        int userId = playerDtoResponseDto.getContent().getId();
        UserWithdrawConfig userWithdrawConfig = new UserWithdrawConfig();
        userWithdrawConfig.setUserId(userId).
                setCreateTime(LocalDateTime.now()).
                setTotalWithdrawMoney(300000.00).
                setTodayWithdrawMoney(300000.00).
                setTotalWithdrawSize(6).
                setTodayWithdrawSize(6);
        userWithdrawConfigService.save(userWithdrawConfig);
        challengeRewardService.insertToday(userId);
        salvageService.insertToday(userId);
        return playerDtoResponseDto;
    }

    private Player getAgentPlayer(Player player) {
        if (player == null) {
            return null;
        }
        if (player.getIsAgent().equals(1)) {
            return player;
        }
        return getAgentPlayer(playerService.getById(player.getSuperId()));
    }

    @PostMapping("/login/{account}/{password}")
    @ApiOperation(value = "用户登录", notes = "需要参数 账号  密码 ")
    public ResponseDto<PlayerDto> login(@ApiParam(value = "账号", required = true)
                                        @PathVariable String account,
                                        @ApiParam(value = "密码", required = true)
                                        @PathVariable String password) {
        BusinessUtil.require(account, BusinessExceptionCode.ACCOUNT);
        BusinessUtil.length(account, BusinessExceptionCode.ACCOUNT, 5, 20);
        BusinessUtil.require(password, BusinessExceptionCode.PASSWORD);
        BusinessUtil.length(password, BusinessExceptionCode.PASSWORD, 6, 20);
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getAccount, account);
        Player player = playerService.getOne(lambdaQueryWrapper);
        BusinessUtil.assertParam(player != null, "用户不存在");
        BusinessUtil.assertParam(
                player.getPassword().equals(MD5Utils.getMD5Str(MD5Utils.getMD5Str(password + player.getSalt()))),
                "密码不正确");
        return PlayerUtils.getPlayerDtoResponseDto(player);
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
        return PlayerUtils.getPlayerDtoResponseDto(player);
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


}

