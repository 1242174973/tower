package com.tower.core.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.constant.Mid;
import com.tower.core.game.TowerGame;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.Player;
import com.tower.msg.Tower;
import com.tower.service.PlayerService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.netty.channel.Channel;
import org.apache.commons.lang3.RandomUtils;

import java.util.stream.Collectors;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/26 16:56
 */
public class PlayerUtils {

    /**
     * 保存玩家信息
     *
     * @param player 玩家
     */
    public static void savePlayer(Player player) {
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        PlayerService playerService = MyApplicationContextUti.getBean(PlayerService.class);
        playerService.saveOrUpdate(player);
        String token = getToken(player.getId());
        redisOperator.hset(RedisVariable.USER_INFO, token, JsonUtils.objectToJson(player));
        Channel channel = MsgBossHandler.getPlayerIdChannel(player.getId());
        if (channel != null) {
            Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
            msgCtn.setType(Mid.PLAYER_INFO_RES);
            msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
            Tower.UserInfoRes.Builder userInfoRes = Tower.UserInfoRes.newBuilder();
            userInfoRes.setAccount(player.getAccount());
            userInfoRes.setId(player.getId());
            userInfoRes.setNickname(player.getNickName());
            userInfoRes.setMoney(player.getMoney().doubleValue());
            userInfoRes.setSafeBox(player.getSafeBox().doubleValue());
            userInfoRes.setVip(player.getVip());
            msgCtn.setDatas(userInfoRes.build().toByteString());
            MsgUtil.sendMsg(channel, msgCtn.build());
        }
    }

    /**
     * 根据player对象获得返回体
     *
     * @param player 玩家
     * @return 返回体
     */
    public static ResponseDto<PlayerDto> getPlayerDtoResponseDto(Player player) {
        PlayerUtils.savePlayer(player);
        PlayerDto userDto = CopyUtil.copy(player, PlayerDto.class);
        String token = PlayerUtils.getToken(player.getId());
        userDto.setToken(token);
        ResponseDto<PlayerDto> responseDto = new ResponseDto<>();
        responseDto.setContent(userDto);
        return responseDto;
    }

    /**
     * 根据player对象获得返回体
     *
     * @param player 玩家
     * @return 返回体
     */
    public static ResponseDto<PlayerDto> getPlayerDtoResponseDtoNotSave(Player player) {
        PlayerDto userDto = CopyUtil.copy(player, PlayerDto.class);
        String token = PlayerUtils.getToken(player.getId());
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
    public static String getToken(int userId) {
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        String token = redisOperator.hget(RedisVariable.USER_TOKEN, userId);
        if (token != null) {
            return token;
        }
        token = UuidUtil.getShortUuid();
        redisOperator.hset(RedisVariable.USER_TOKEN, userId, token);
        return token;
    }

    /**
     * 获得token
     */
    public static void setNewToken(Player player) {
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        String token = redisOperator.hget(RedisVariable.USER_TOKEN, player.getId());
        redisOperator.hdel(RedisVariable.USER_TOKEN, String.valueOf(player.getId()));
        redisOperator.hdel(RedisVariable.USER_INFO, token);
        token = UuidUtil.getShortUuid();
        redisOperator.hset(RedisVariable.USER_TOKEN, player.getId(), token);
        redisOperator.hset(RedisVariable.USER_INFO, token, JsonUtils.objectToJson(player));
    }

    public static Player getPlayer(int id) {
        String token = getToken(id);
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        Player player = JsonUtils.jsonToPojo(redisOperator.hget(RedisVariable.USER_INFO, token), Player.class);
        if (player == null) {
            PlayerService playerService = MyApplicationContextUti.getBean(PlayerService.class);
            player = playerService.getById(id);
        }
        return player;
    }

    /**
     * 查询不会重复的邀请码
     *
     * @return 邀请码
     */
    public static String getShortUuid() {
        String shortUuid = UuidUtil.getShortUuid(4);
        PlayerService playerService = MyApplicationContextUti.getBean(PlayerService.class);
        LambdaQueryWrapper<Player> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Player::getSpread, shortUuid);
        Player one = playerService.getOne(lambdaQueryWrapper);
        if (one != null) {
            return getShortUuid();
        }
        return shortUuid;
    }

    public static void removePlayer(int id) {
        RedisOperator redisOperator = MyApplicationContextUti.getBean(RedisOperator.class);
        String token = redisOperator.hget(RedisVariable.USER_TOKEN, id);
        redisOperator.hdel(RedisVariable.USER_TOKEN, String.valueOf(id));
        redisOperator.hdel(RedisVariable.USER_INFO, token);
        PlayerService playerService = MyApplicationContextUti.getBean(PlayerService.class);
        playerService.removeById(id);
        MsgBossHandler.getRoomUserIds().remove(id);
        Channel playerIdChannel = MsgBossHandler.getPlayerIdChannel(id);
        playerIdChannel.close();
        TowerGame towerGame = MyApplicationContextUti.getBean(TowerGame.class);
        towerGame.getBetLogList().removeAll(towerGame.getBetLogList()
                .stream().filter(betLog -> betLog.getUserId().equals(id))
                .collect(Collectors.toList()));
    }
}
