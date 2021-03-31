package com.tower.handler.base;

import com.tower.core.AbsLogicHandler;
import com.tower.core.ILogicHandler;
import com.tower.core.constant.Mid;
import com.tower.msg.Tower;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/31 13:20
 */
@Component
@Slf4j
public class RecordHandler extends AbsLogicHandler<Tower.RoomReq> implements Mid, ILogicHandler<Tower.RoomReq> {
    @Override
    public int getMid() {
        return MID_RECORD_REQ;
    }

    @Override
    public void handle(Tower.RoomReq reqMsg, Channel ch, Long userId) {

    }
}
