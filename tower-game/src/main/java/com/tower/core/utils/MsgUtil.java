package com.tower.core.utils;

import com.google.protobuf.MessageLite;
import com.tower.core.constant.GameConst;
import com.tower.msg.Tower;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 */
@Slf4j
public class MsgUtil {
    /**
     * 向Ch发送消息
     *
     * @param ch
     * @param mid
     * @param sendMsg
     */
    public static ChannelFuture sendMsg(Channel ch, int mid, MessageLite sendMsg, int reqMsgId) {
        log.info("发送消息给{},mid:{},id:{}", ch.attr(GameConst.ATTR_USER_ID).get(), mid, reqMsgId);
        return ch.writeAndFlush(buildRes(mid, sendMsg, reqMsgId));
    }

    public static ChannelFuture sendMsg(Channel ch, Tower.MsgCtn msgCtn) {
        return sendMsg(ch, msgCtn.toByteArray());
    }

    public static ChannelFuture sendMsg(Channel ch, byte[] message) {
        log.info("发送消息给{}", ch.attr(GameConst.ATTR_USER_ID).get());
        ByteBuf buf = null;
        if (message != null) {
            byte[] bytes = intToBytes(124512);
            bytes = byteMerger(bytes, message);
            buf = Unpooled.wrappedBuffer(bytes);
        }
        return ch.writeAndFlush(new BinaryWebSocketFrame(buf));
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * java 合并两个byte数组
     *
     * @param byte_1
     * @param byte_2
     * @return
     */
    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static void sendMsg(Channel ch, int mid, MessageLite sendMsg) {
        sendMsg(ch, mid, sendMsg, 0);
    }

    public static Tower.MsgCtn buildRes(int mid, MessageLite sendMsg, int reqMsgId) {
        Tower.MsgCtn.Builder msgBuilder = Tower.MsgCtn.newBuilder();
        msgBuilder.setType(mid);
        msgBuilder.setReqMsgId(reqMsgId);
        msgBuilder.setDatas(sendMsg.toByteString());
        return msgBuilder.build();
    }


}
