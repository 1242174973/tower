package com.tower.core.pipline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author xxxx
 * @date2021/3/17 10:34
 */
public class ProtoLenDecodeHandler extends ByteToMessageDecoder {
    private static int PASSWORD = 29099;
    private static int MAX_SIZE = 10240;
    private static int INTEGER_BYTE_SIZE = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
       /* //这个方法接收
        try {
            int len = in.readableBytes();
            if (len < INTEGER_BYTE_SIZE) {
                return;
            }
            byte[] bytes = new byte[INTEGER_BYTE_SIZE];
            in.readBytes(bytes, 0, INTEGER_BYTE_SIZE);
            int password = bytesToInt(bytes);
            if (password != PASSWORD) {
                ctx.close();
                return;
            }
            if (len > MAX_SIZE) {
                System.err.println("数据大于10m");
            }
            bytes = new byte[len - INTEGER_BYTE_SIZE];
            in.readBytes(bytes, 0, len - INTEGER_BYTE_SIZE);
            Tower.MsgCtn commonMessage = Tower.MsgCtn.PARSER.parseFrom(bytes, 0, len - INTEGER_BYTE_SIZE);
            out.add(commonMessage);
//            MsgUtil.sendMsg(ctx.channel(), commonMessage);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }*/
    }

    private int readLengthHead(ByteBuf in) {
        if (in.readableBytes() < 4) {
            return 0;
        }
        byte[] lengthBytes = new byte[4];
        in.readBytes(lengthBytes, 0, 4);
        return bytesToInt(lengthBytes);
    }

    public int bytesToInt(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24));
        return value;
    }
}
