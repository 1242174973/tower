package com.tower.core;

import com.tower.core.pipline.MsgBossHandler;
import com.tower.core.pipline.ProtoLenDecodeHandler;
import com.tower.core.pipline.ProtoLenEncodeHandler;
import com.tower.msg.Tower;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * @author xxxx
 * 定时心跳处理 p.addLast(new IdleStateHandler(20, 0, 4, TimeUnit.SECONDS));
 * @date2021/3/17 10:32
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final String WEBSOCKET_PATH = "/websocket";
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("Compression", new WebSocketServerCompressionHandler());
        pipeline.addLast("Protocol", new WebSocketServerProtocolHandler(ServerInitializer.WEBSOCKET_PATH, null, true));
//        pipeline.addLast(new ProtoLenDecodeHandler());
        pipeline.addLast(new MsgBossHandler());


    }
}
