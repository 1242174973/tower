package com.tower.core.pipline;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tower.Hall;
import com.tower.TowerApplication;
import com.tower.core.ILogicHandler;
import com.tower.core.MsgMapping;
import com.tower.core.constant.GameConst;
import com.tower.core.constant.Mid;
import com.tower.core.constant.MidTypeUtil;
import com.tower.core.thread.LogDefThreadFactory;
import com.tower.core.utils.MsgUtil;
import com.tower.exception.BusinessException;
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
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author xxxx
 * @date2021/3/17 10:38
 */
@Slf4j
public class MsgBossHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final ExecutorService EXECUTE_LOGIN = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());
    private static final ExecutorService EXECUTE_GAME = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());
    private static final ExecutorService EXECUTE_RECORD = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());
    private static final ExecutorService EXECUTE_ROOM = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());

    public static final ExecutorService EXECUTE_BET_LOG_SAVE = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());


    private static Map<Integer, Channel> playerIdChannel = new ConcurrentHashMap<>();
    private static Set<Integer> roomUserIds = new CopyOnWriteArraySet<>();

    private static MsgMapping msgMapping = null;
    private static Hall hall = null;

    private static final int PASSWORD = 29099;
    private static final int MAX_SIZE = 10240;

    static {
        msgMapping = MyApplicationContextUti.getBean(MsgMapping.class);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, WebSocketFrame frame) {
        //??????????????????
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
                    System.err.println("????????????10m");
                }
                bytes = new byte[len - Integer.BYTES];
                frame.content().readBytes(bytes, 0, len - Integer.BYTES);
                Tower.MsgCtn commonMessage = Tower.MsgCtn.PARSER.parseFrom(bytes, 0, len - Integer.BYTES);
                doing(ctx, commonMessage);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            log.error("can't process the frame???" + frame.toString());
        }
    }

    private void doing(ChannelHandlerContext ctx, Tower.MsgCtn msg) {
        if (MidTypeUtil.isHeartBeat(msg)) {
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_HEART_BEAT_RES, Tower.HeartBeatRes.getDefaultInstance());
        } else if (MidTypeUtil.isLogin(msg)) {
            EXECUTE_LOGIN.execute(() -> doIt(ctx, msg));
        } else if (MidTypeUtil.isGame(msg)) {
            EXECUTE_GAME.execute(() -> doIt(ctx, msg));
        } else if (MidTypeUtil.isRecord(msg)) {
            EXECUTE_RECORD.execute(() -> doIt(ctx, msg));
        } else if (MidTypeUtil.isRoom(msg)) {
            EXECUTE_ROOM.execute(() -> doIt(ctx, msg));
        }
    }

    private void doIt(ChannelHandlerContext ctx, Tower.MsgCtn msg) {
        Long userId = ctx.channel().attr(GameConst.ATTR_USER_ID).get();
        try {
            if(TowerApplication.status==1){
                Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
                errRes.setReqMsgId(msg.getReqMsgId());
                errRes.setErrorMsg("???????????????");
                errRes.setErrorCode(GameConst.SysErrorCode.LOGIN_NULL);
                MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
                return;
            }
            ILogicHandler<Object> handler = (ILogicHandler<Object>) msgMapping.findHandle(msg.getType());
            if (handler == null) {
                log.warn("Unknown Msg Id:{},UID[{}],CH[{}]", msg.getType(), userId, ctx.channel().id());
                throw new InvalidProtocolBufferException("?????????????????????");
            }
            Object o = handler.parseProto(msg.getDatas());
            if (handler.needLogin() && userId == null) {
                log.warn("????????????,??????????????????,MID:[{}],CH[{}]", msg.getType(), ctx.channel().id());

                Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
                errRes.setReqMsgId(msg.getReqMsgId());
                errRes.setErrorMsg("???????????????,???????????????");
                errRes.setErrorCode(GameConst.SysErrorCode.LOGIN_NULL);
                MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
                return;
            }
            handler.handle(o, ctx.channel(), userId);
        } catch (ServerException e) {
            log.warn("??????????????????,??????:{},??????mid:{},msg:{},CH[{}]", userId, msg.getType(), e.getMessage(), ctx.channel().id());
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
            log.warn("??????????????????(??????),??????:{},??????mid:{},CH[{}]", userId, msg.getType(), ctx.channel().id(), e);
            Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
            errRes.setReqMsgId(msg.getReqMsgId());
            errRes.setErrorMsg("????????????,????????????????????????,????????????????????????????????????");
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
        } catch (Exception e) {
            log.error("??????????????????,??????:{},??????mid:{},CH[{}]", userId, msg.getType(), ctx.channel().id(), e);
            Tower.ServerErrorRes.Builder errRes = Tower.ServerErrorRes.newBuilder();
            errRes.setReqMsgId(msg.getReqMsgId());
            errRes.setErrorMsg("??????????????????,??????mid:" + msg.getType() + ",error:" + e.getMessage());
            MsgUtil.sendMsg(ctx.channel(), Mid.MID_SERVER_ERROR_RES, errRes.build());
        }
    }

    public static int bytesToInt(byte[] src) {
        return bytesToInt(src, 0);
    }

    /**
     * byte????????????int???????????????????????????(???????????????????????????)??????????????????intToBytes??????????????????
     *
     * @param src    byte??????
     * @param offset ???????????????offset?????????
     * @return int??????
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = ((src[offset] & 0xFF)
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
            Channel channel = playerIdChannel.get(userId.intValue());
            if (!channel.isActive()) {
                log.info("??????{}??????[{}]??????,??????channel???ATTR_USER_ID", userId, ctx.channel().id());
                userIdAttr.set(null);
                playerIdChannel.remove(userId.intValue());
                roomUserIds.remove(userId.intValue());
            } else {
                log.info("??????");
            }
        } else {
            log.info("????????????[{}]??????", ctx.channel().id());
        }
        super.channelUnregistered(ctx);
    }

    /**
     * ????????????????????????????????? ??????
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                // ????????????channel
                log.warn("UID[{}]???????????????????????????????????????????????????", ctx.channel().attr(GameConst.ATTR_USER_ID).get());
                ctx.close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                // ????????????channel
                log.warn("UID[{}]???????????????????????????????????????????????????", ctx.channel().attr(GameConst.ATTR_USER_ID).get());
                ctx.close();
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                log.warn("UID[{}]???????????????????????????????????????", ctx.channel().attr(GameConst.ATTR_USER_ID).get());
                // ??????????????????    ??????????????????
                MsgUtil.sendMsg(ctx.channel(), Mid.MID_HEART_BEAT_RES, Tower.HeartBeatRes.getDefaultInstance());
            }
        }
    }

    private final String CON_RESET_BY???PER = "Connection reset by peer";
    private final String CON_TIMED_OUT = "Connection timed out";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException
                && cause.getMessage() != null
                && (CON_RESET_BY???PER.equalsIgnoreCase(cause.getMessage())
                || CON_TIMED_OUT.equalsIgnoreCase(cause.getMessage()))) {
            log.warn("????????????CH[{}]??????:{}", ctx.channel().id(), cause.getMessage());
        } else {
            log.error("?????????CH[{}]??????:{}", ctx.channel().id(), cause.getMessage(), cause);
        }
    }

    public static void putPlayerIdChannel(int playerId, Channel channel) {
        playerIdChannel.put(playerId, channel);
    }

    public static Channel getPlayerIdChannel(int playerId) {
        return playerIdChannel.get(playerId);
    }

    public static Set<Integer> getRoomUserIds() {
        return roomUserIds;
    }
}
