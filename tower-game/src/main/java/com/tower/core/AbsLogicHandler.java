package com.tower.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.tower.exception.ServerException;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 15:17
 */
public abstract class AbsLogicHandler<T extends MessageLite> {


    private MessageLite prototype;

    public void initPrototype(){
        try {
            Type t = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) t).getActualTypeArguments();
            @SuppressWarnings("unchecked")
            Method method = ((Class<T>) params[0]).getMethod("getDefaultInstance");
            prototype = (MessageLite)method.invoke(null);
        } catch (Exception e) {
            throw new ServerException("onMsgArrive parse ByteString to Proto error.",e);
        }
    }

    /**
     * 消息ID
     * @return
     */
    public abstract int getMid();

    public boolean needLogin(){
        return true;
    }

    public abstract void handle(T reqMsg, Channel ch, Long userId);


    /**
     * 当消息到达需要处理
     * @param bs
     * @throws InvalidProtocolBufferException
     */
    @SuppressWarnings("unchecked")
    public T parseProto(ByteString bs) throws ServerException, InvalidProtocolBufferException{
        return (T)prototype.getParserForType().parseFrom(bs);
    }
}
