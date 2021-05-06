package com.tower.core.thread;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @author xxxx
 * @date2021/3/17 11:02
 */
public class ExecuteHashedWheelTimer {
    private HashedWheelTimer wheelTimer = new HashedWheelTimer();
    private ExecutorService excutor = new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new LogDefThreadFactory());

    public Timeout newTimeout(final Runnable task, long delay, TimeUnit unit) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                excutor.execute(task);
            }
        };
        return wheelTimer.newTimeout(timerTask, delay, unit);
    }

    public static void main(String[] args) {
//        ExecuteHashedWheelTimer executeHashedWheelTimer=new ExecuteHashedWheelTimer();
//        executeHashedWheelTimer.newTimeout(()-> {
//            System.out.println(LocalDateTime.now());
//            executeHashedWheelTimer.newTimeout(()-> System.out.println(LocalDateTime.now()),5,TimeUnit.SECONDS);
//        },5,TimeUnit.SECONDS);
    }
}
