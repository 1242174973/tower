package com.tower.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tower.exception.ServerException;
import io.netty.channel.Channel;

/**
 * @author xxxx
 * @date2021/3/17 10:18
 */
public interface ILogicHandler<T> {
    void initPrototype();

    int getMid();

    boolean needLogin();

    void handle(T reqMsg, Channel ch, Long userId);

    T parseProto(ByteString bs) throws ServerException, InvalidProtocolBufferException;
}
