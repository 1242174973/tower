package com.tower;

import com.tower.core.MsgMapping;
import com.tower.core.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:12
 */
@Component
@Slf4j
public class GameServer {
    @Autowired
    private MsgMapping msgMapping;

    @Value("${netty.port}")
    private int nettyPort;
    public static String serverId = "defaultServer";
    public void start(){
        //协议映射处理器
        msgMapping.init();
        Thread th = new Thread(()->{
                int port = nettyPort;
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .option(ChannelOption.SO_BACKLOG, 10240)
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .childOption(ChannelOption.TCP_NODELAY, true)
                            .childHandler(new ServerInitializer());
                    try {
                        b.bind(port).sync().channel().closeFuture().sync();
                    } catch (InterruptedException e) {
                        log.error("start error",e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
        },"GameByMeehu");
        th.start();
    }
}
