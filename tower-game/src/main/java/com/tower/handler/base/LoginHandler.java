package com.tower.handler.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.GameConst;
import com.tower.core.constant.Mid;
import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.utils.MsgUtil;
import com.tower.entity.Notice;
import com.tower.entity.Player;
import com.tower.enums.GameCmd;
import com.tower.msg.Tower;
import com.tower.service.NoticeService;
import com.tower.utils.JsonUtils;
import com.tower.utils.RedisOperator;
import com.tower.utils.UuidUtil;
import com.tower.variable.RedisVariable;
import io.netty.channel.Channel;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 15:14
 */
@Component
public class LoginHandler extends AbsLogicHandler<Tower.LoginReq> implements Mid, ILogicHandler<Tower.LoginReq> {


    @Resource
    private RedisOperator redisOperator;

    @Resource
    private NoticeService noticeService;

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
            Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
            msgCtn.setDatas(builder.build().toByteString());
            msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
            msgCtn.setType(Mid.MID_LOGIN_RES);
            MsgUtil.sendMsg(ch,msgCtn.build());
            return;
        }
        // 这边拿到的 token  前往redis获得用户信息返回
        Player player = JsonUtils.jsonToPojo(redisOperator.hget(RedisVariable.USER_INFO, token), Player.class);
        if (StringUtils.isEmpty(player)) {
            Tower.LoginRes.Builder builder = Tower.LoginRes.newBuilder();
            builder.setSuc(false);
            builder.setMsg("登录失败，token以失效");
            Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
            msgCtn.setDatas(builder.build().toByteString());
            msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
            msgCtn.setType(Mid.MID_LOGIN_RES);
            MsgUtil.sendMsg(ch,msgCtn.build());
            return;
        }
        ch.attr(GameConst.ATTR_USER_ID).set(player.getId().longValue());
        Channel channel = MsgBossHandler.getPlayerIdChannel(player.getId());
        if (channel != null) {
            sendLoginTop(channel);
        }
        sendLoginSuccess(ch, player);
        MsgBossHandler.putPlayerIdChannel(player.getId(), ch);
        sendNotice(ch);
    }

    /**
     * 发送公告
     *
     * @param ch 连接
     */
    private void sendNotice(Channel ch) {
        List<Notice> notices = noticeService.getBaseMapper().selectList(new LambdaQueryWrapper<>());
        if (notices.size() > 0) {
            Notice notice = notices.get(0);
            Tower.Notice.Builder tower = Tower.Notice.newBuilder();
            tower.setContent(notice.getContent());
            tower.setCreateTime(notice.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            Tower.GameRes.Builder gameRes= Tower.GameRes.newBuilder();
            gameRes.setCmd(GameCmd.NOTICE.getCode()).setCountdown(0);
            Tower.MsgCtn.Builder msgCtn = Tower.MsgCtn.newBuilder();
            msgCtn.setDatas(gameRes.build().toByteString());
            msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
            msgCtn.setType(Mid.MID_GAME_RES);
            MsgUtil.sendMsg(ch, msgCtn.build());
        }
    }

    private void sendLoginTop(Channel channel) {
        Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
        errRes.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
        errRes.setErrorMsg("账号在异地登录");
        errRes.setErrorCode(GameConst.SysErrorCode.LOGIN_TOP);
        MsgUtil.sendMsg(channel, Mid.MID_SERVER_ERROR_RES, errRes.build());
        channel.close();
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
        msgCtn.setReqMsgId(RandomUtils.nextInt(0, Integer.MAX_VALUE));
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
