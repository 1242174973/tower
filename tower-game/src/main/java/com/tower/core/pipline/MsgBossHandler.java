package com.tower.core.pipline;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tower.Hall;
import com.tower.core.ILogicHandler;
import com.tower.core.MsgMapping;
import com.tower.core.constant.GameConst;
import com.tower.core.constant.Mid;
import com.tower.core.constant.MidTypeUtil;
import com.tower.core.thread.LogDefThreadFactory;
import com.tower.core.utils.MsgUtil;
import com.tower.exception.ServerException;
import com.tower.msg.Tower;
import com.tower.utils.MyApplicationContextUti;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:38
 */
@Slf4j
public class MsgBossHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final ExecutorService executeLogin = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());
    private static final ExecutorService executeGame = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());
    private static final ExecutorService executeRecord = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());

    private static Map<Integer, Channel> playerIdChannel = new ConcurrentHashMap<>();


    private static MsgMapping msgMapping = null;
    private static Hall hall = null;

    private static final int PASSWORD = 29099;
    private static final int MAX_SIZE = 10240;

    static {
        msgMapping = MyApplicationContextUti.getBean(MsgMapping.class);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, WebSocketFrame frame) {
        //这个方法接收
        if ((frame instanceof BinaryWebSocketFrame)) {
            try {
                int len = frame.content().readableBytes();
                if (len < Integer.BYTES) {
                    return;
                }
                byte[] bytes = new byte[Integer.BYTES];
                frame.content().readBytes(bytes, 0, Integer.BYTES);
                int password = bytesToInt(bytes);
                if (password != PASSWORD) {
                    ctx.close();
                    return;
                }
                if (len > MAX_SIZE) {
                    System.err.println("数据大于10m");
                }
                bytes = new byte[len - Integer.BYTES];
                frame.content().readBytes(bytes, 0, len - Integer.BYTES);
                Tower.MsgCtn commonMessage = Tower.MsgCtn.PARSER.parseFrom(bytes, 0, len - Integer.BYTES);
                doing(ctx, commonMessage);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            log.error("can't process the frame！" + frame.toString());
        }
    }

    private void doing(ChannelHandlerContext ctx, Tower.MsgCtn msg) {
        MsgUtil.sendMsg(ctx.channel(), msg);
        if (MidTypeUtil.isHeartBeat(msg)) {
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_HEART_BEAT_RES, Tower.HeartBeatRes.getDefaultInstance());
        } else if (MidTypeUtil.isLogin(msg)) {
            executeLogin.execute(() -> doIt(ctx, msg));
        } else if (MidTypeUtil.isGame(msg)) {
            executeGame.execute(() -> doIt(ctx, msg));
        } else if (MidTypeUtil.isRecord(msg)) {
            executeRecord.execute(() -> doIt(ctx, msg));
        }
    }

    private void doIt(ChannelHandlerContext ctx, Tower.MsgCtn msg) {
        Long userId = ctx.channel().attr(GameConst.ATTR_USER_ID).get();
        try {
            ILogicHandler<Object> handler = (ILogicHandler<Object>) msgMapping.findHandle(msg.getType());
            if (handler == null) {
                log.warn("Unknown Msg Id:{},UID[{}],CH[{}]", msg.getType(), userId, ctx.channel().id());
                throw new InvalidProtocolBufferException("未知的游戏协议");
            }
            Object o = handler.parseProto(msg.getDatas());
            if (handler.needLogin() && userId == null) {
                log.warn("连接存在,但登录已失效,MID:[{}],CH[{}]", msg.getType(), ctx.channel().id());

                Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
                errRes.setReqMsgId(msg.getReqMsgId());
                errRes.setErrorMsg("登录已失效,请重新登录");
                errRes.setErrorCode(GameConst.SysErrorCode.LOGIN_NULL);
                MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
                return;
            }
            handler.handle(o, ctx.channel(), userId);
        } catch (ServerException e) {
            log.warn("消息处理失败,用户:{},消息mid:{},msg:{},CH[{}]", userId, msg.getType(), e.getMessage(), ctx.channel().id());
            Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
            errRes.setReqMsgId(msg.getReqMsgId());
            errRes.setErrorMsg(e.getMessage());
            if (e.getMsgExt() != null) {
                errRes.setErrorMsgExt(e.getMsgExt());
            }
            if (e.getMsgCode() != null) {
                errRes.setErrorCode(e.getMsgCode());
            }
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
        } catch (InvalidProtocolBufferException e) {
            log.warn("消息处理失败(协议),用户:{},消息mid:{},CH[{}]", userId, msg.getType(), ctx.channel().id(), e);
            Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
            errRes.setReqMsgId(msg.getReqMsgId());
            errRes.setErrorMsg("操作失败,您的版本可能过低,请关闭游戏并重新打开再试");
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
        } catch (Exception e) {
            log.error("消息处理失败,用户:{},消息mid:{},CH[{}]", userId, msg.getType(), ctx.channel().id(), e);
            Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
            errRes.setReqMsgId(msg.getReqMsgId());
            errRes.setErrorMsg("消息处理失败,消息mid:" + msg.getType() + ",error:" + e.getMessage());
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
        }
    }

    public static int bytesToInt(byte[] src) {
        return bytesToInt(src, 0);
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Attribute<Long> userIdAttr = ctx.channel().attr(GameConst.ATTR_USER_ID);
        if (hall == null) {
            hall = MyApplicationContextUti.getBean(Hall.class);
        }
        final Long userId = userIdAttr.get();
        if (userId != null) {
            log.info("用户{}连接[{}]断开,清空channel的ATTR_USER_ID", userId, ctx.channel().id());
            userIdAttr.set(null);
            executeLogin.execute(() -> {
                //TODO 处理玩家断开连接
                playerIdChannel.remove(userId.intValue());
            });

        } else {
            log.info("用户连接[{}]断开", ctx.channel().id());
        }

        // 断线  再次关闭,防止断的不干净
        if (ctx.channel() != null) {
            try {
                if (ctx.channel().isActive()
                        || ctx.channel().isOpen()
                        || ctx.channel().isRegistered()
                        || ctx.channel().isWritable()) {
                    log.info("再次主动关闭用户Ch[{}],UID[{}]", ctx.channel().id(), userId);
                    ctx.channel().close();
                }
            } catch (Exception e) {
                log.error("关闭ch[{}]异常[" + userId + "]", ctx.channel().id(), e);
            }
        }
        super.channelUnregistered(ctx);
    }

    /**
     * 一段时间未进行读写操作 回调
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                // 超时关闭channel
                log.warn("UID[{}]服务器超时未收到任何信息，断开连接", ctx.channel().attr(GameConst.ATTR_USER_ID).get());
                ctx.close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                // 超时关闭channel
                log.warn("UID[{}]服务器超时未发出任何数据，断开连接", ctx.channel().attr(GameConst.ATTR_USER_ID).get());
                ctx.close();
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                log.warn("UID[{}]无读无写之后，发送心跳消息", ctx.channel().attr(GameConst.ATTR_USER_ID).get());
                // 无读无写之后    发送心跳消息
                MsgUtil.sendMsg(ctx.channel(), Mid.MID_HEART_BEAT_RES, Tower.HeartBeatRes.getDefaultInstance());
            }
        }
    }

    private final String CON_RESET_BY＿PER = "Connection reset by peer";
    private final String CON_TIMED_OUT = "Connection timed out";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException
                && cause.getMessage() != null
                && (CON_RESET_BY＿PER.equalsIgnoreCase(cause.getMessage())
                || CON_TIMED_OUT.equalsIgnoreCase(cause.getMessage()))) {
            log.warn("用户连接CH[{}]异常:{}", ctx.channel().id(), cause.getMessage());
        } else {
            log.error("未知的CH[{}]异常:{}", ctx.channel().id(), cause.getMessage(), cause);
        }
    }

    public static void putPlayerIdChannel(int playerId, Channel channel) {
        playerIdChannel.put(playerId, channel);
    }

    public static Channel getPlayerIdChannel(int playerId) {
        return playerIdChannel.get(playerId);
    }
}
