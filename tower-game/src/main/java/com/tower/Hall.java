package com.tower;

import com.tower.core.thread.ExecuteHashedWheelTimer;
import com.tower.msg.Tower;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 11:00
 */
@Slf4j
@Component
public class Hall {
    private ExecuteHashedWheelTimer timer = new ExecuteHashedWheelTimer();

    private ChannelGroup chGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void handleGameRecord(ChannelHandlerContext ctx, Tower.MsgCtn msg) {
        try {
            handleGameRecordReal(ctx, msg);
        } catch (Exception e) {
            log.error("记录游戏战绩记录异常:[{}]", e.getMessage(), e);
        }
    }

    private void handleGameRecordReal(ChannelHandlerContext ctx, Tower.MsgCtn msg) {

    }

}
