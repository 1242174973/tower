/*
package com.tower.core.rabbit;

import com.tower.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

*/
/**
 * @author 梦-屿-千-寻
 * @date 2021/3/27 10:59
 *//*

@Component
@RabbitListener(queues = RabbitConfig.QUEUE_A)
@Slf4j
public class MsgReceiver {
    @RabbitHandler
    public void process(String content) {
        log.info("接收处理队列A当中的消息： " + content);
    }
}
*/
