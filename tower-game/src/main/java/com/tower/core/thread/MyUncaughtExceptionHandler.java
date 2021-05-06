package com.tower.core.thread;

import com.tower.exception.ServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xxxx
 * @date2021/3/17 10:58
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof ServerException) {
            log.warn("线程[{}][{}]内逻辑错误:{}",t.getId(),t.getName(),e.getMessage());
        }else{
            log.error("线程[{}][{}]内逻辑错误:{}",t.getId(),t.getName(),e.getMessage(),e);
        }
    }
}
