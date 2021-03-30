package com.tower.handler.base;

import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.Mid;
import com.tower.msg.Tower;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/30 11:36
 */
@Component
public class RoomHandler extends AbsLogicHandler<Tower.RoomReq> implements Mid, ILogicHandler<Tower.RoomReq> {
    @Override
    public int getMid() {
        return MID_ROOM_REQ;
    }

    @Override
    public void handle(Tower.RoomReq reqMsg, Channel ch, Long userId) {

    }
}
