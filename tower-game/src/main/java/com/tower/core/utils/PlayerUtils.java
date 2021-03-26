package com.tower.core.utils;

import com.tower.core.constant.Mid;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.dto.PlayerDto;
import com.tower.dto.ResponseDto;
import com.tower.entity.Player;
import com.tower.msg.Tower;
import com.tower.service.PlayerService;
import com.tower.utils.*;
import com.tower.variable.RedisVariable;
import io.netty.channel.Channel;

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
            Tower.UserInfoRes.Builder userInfoRes = Tower.UserInfoRes.newBuilder();
            userInfoRes.setAccount(player.getAccount());
            userInfoRes.setId(player.getId());
            userInfoRes.setNickname(player.getNickName());
            userInfoRes.setMoney(player.getMoney().doubleValue());
            userInfoRes.setSafeBox(player.getSafeBox().doubleValue());
            msgCtn.setDatas(userInfoRes.build().toByteString());
            MsgUtil.sendMsg(channel, msgCtn.build().toByteArray());
        }
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
}
