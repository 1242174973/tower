package com.tower.handler.base;

import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.GameConst;
import com.tower.core.constant.Mid;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.MsgUtil;
import com.tower.entity.Player;
import com.tower.msg.Tower;
import com.tower.utils.JsonUtils;
import com.tower.utils.RedisOperator;
import com.tower.variable.RedisVariable;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 15:14
 */
@Component
public class LoginHandler extends AbsLogicHandler<Tower.LoginReq> implements Mid, ILogicHandler<Tower.LoginReq> {


    @Resource
    private RedisOperator redisOperator;

    @Override
    public int getMid() {
        return MID_LOGIN_REQ;
    }

    @Override
    public void handle(Tower.LoginReq reqMsg, Channel ch, Long userId) {
        String token = reqMsg.getToken();
        if (StringUtils.isEmpty(token)) {
            Tower.LoginRes.Builder builder = Tower.LoginRes.newBuilder();
            builder.setSuc(false);
            builder.setMsg("token不能为空");
            return;
        }
        // 这边拿到的 token  前往redis获得用户信息返回
        Player player = JsonUtils.jsonToPojo(redisOperator.hget(RedisVariable.USER_INFO, token), Player.class);
        if (StringUtils.isEmpty(player)) {
            Tower.LoginRes.Builder builder = Tower.LoginRes.newBuilder();
            builder.setSuc(false);
            builder.setMsg("登录失败，token以失效");
            return;
        }
        ch.attr(GameConst.ATTR_USER_ID).set(player.getId().longValue());
        sendLoginSuccess(ch, player);
        MsgBossHandler.putPlayerIdChannel(player.getId(), ch);
    }

    @Override
    public boolean needLogin() {
        return false;
    }

    /**
     * 发送登录成功
     *
     * @param player w
     */
    private void sendLoginSuccess(Channel ch, Player player) {
        Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
        msgCtn.setType(Mid.MID_LOGIN_RES);
        Tower.LoginRes.Builder builder = Tower.LoginRes.newBuilder();
        builder.setSuc(true);
        Tower.UserInfoRes.Builder userInfoRes = Tower.UserInfoRes.newBuilder();
        userInfoRes.setAccount(player.getAccount());
        userInfoRes.setId(player.getId());
        userInfoRes.setNickname(player.getNickName());
        userInfoRes.setMoney(player.getMoney().doubleValue());
        userInfoRes.setSafeBox(player.getSafeBox().doubleValue());
        builder.setUserInfo(userInfoRes);
        msgCtn.setDatas(builder.build().toByteString());
        MsgUtil.sendMsg(ch, msgCtn.build().toByteArray());
    }
}
