package com.tower.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP通知：
 * 1. 前置通知：在方法调用之前执行
 * 2. 后置通知：在方法正常调用之后执行
 * 3. 环绕通知：在方法调用之前和之后，都分别可以执行的通知
 * 4. 异常通知：如果在方法调用过程中发生异常，则通知
 * 5. 最终通知：在方法调用之后执行
 */

/**
 * @author xxxx
 * @date2021/1/16 16:16
 */
@Aspect
@Component
public class ServiceLogAspect {
    /**
     * 日志打印
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogAspect.class);
    /**
     * 超时执行时间 错误级别
     */
    public static final int ERROR_TIME = 3000;
    /**
     * 超时执行时间 警告级别
     */
    public static final int WARN_TIME = 2000;

    /**
     * 切面表达式：
     * execution 代表所要执行的表达式主体
     * 第一处 * 代表方法返回类型 *代表所有类型
     * 第二处 包名代表aop监控的类所在的包
     * 第三处 .. 代表该包以及其子包下的所有类方法
     * 第四处 * 代表类名，*代表所有类
     * 第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.jun.controller..*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.info("====== 开始执行 {}.{} ======",
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName());

        // 记录开始时间
        long begin = System.currentTimeMillis();

        // 执行目标 service
        Object result = joinPoint.proceed();

        // 记录结束时间
        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime > ERROR_TIME) {
            LOGGER.error("====== 执行结束，耗时：{} 毫秒 ======", takeTime);
        } else if (takeTime > WARN_TIME) {
            LOGGER.warn("====== 执行结束，耗时：{} 毫秒 ======", takeTime);
        } else {
            LOGGER.info("====== 执行结束，耗时：{} 毫秒 ======", takeTime);
        }

        return result;
    }
}
