package com.tower.core.pipline;

import com.tower.Hall;
import com.tower.msg.Tower;
import com.tower.utils.MyApplicationContextUti;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:35
 */
public class ProtoLenEncodeHandler extends MessageToByteEncoder<Tower.MsgCtn> {
    private Logger log = LoggerFactory.getLogger(ProtoLenEncodeHandler.class);

    private static Hall hall;

    @Override
    protected void encode(ChannelHandlerContext ctx, Tower.MsgCtn msg, ByteBuf out) throws Exception {
        try {
            int bodyLen = msg.getSerializedSize();
            out.ensureWritable(4 + bodyLen);
            out.writeBytes(intToBytes(bodyLen), 0, 4);
            out.writeBytes(msg.toByteArray(), 0, bodyLen);
            if (hall == null) {
                hall = MyApplicationContextUti.getBean(Hall.class);
            }
            hall.handleGameRecord(ctx, msg);
            log.info("{},encode msg type:{},bodyLen:{} suc.", Thread.currentThread().getName(), msg.getType(), bodyLen);
        } catch (Exception e) {
            log.error("编码数据异常", e);
        }
    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }
}
