package com.tower.core.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:57
 */
public class LogDefThreadFactory implements ThreadFactory {
    private ThreadFactory t = Executors.defaultThreadFactory();
    @Override
    public Thread newThread(Runnable runnable) {
        Thread th = t.newThread(runnable);
        th.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        return th;
    }
}
