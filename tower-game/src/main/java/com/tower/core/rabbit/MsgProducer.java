/*
package com.tower.core.rabbit;

import com.tower.config.RabbitConfig;
import com.tower.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

*/
/**
 * @author xxxx
 * @date 2021/3/27 10:54
 *//*

@Slf4j
@Component
public class MsgProducer implements RabbitTemplate.ConfirmCallback {

    */
/**
     * 由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
     *//*

    private RabbitTemplate rabbitTemplate;

    */
/**
     * 构造方法注入rabbitTemplate
     *//*

    @Autowired
    public MsgProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
        rabbitTemplate.setConfirmCallback(this);
    }

    public void sendMsg(String content) {
        CorrelationData correlationId = new CorrelationData(UuidUtil.getUuid());
        //把消息放入ROUTINGKEY_A对应的队列当中去，对应的是队列A
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A, RabbitConfig.ROUTINGKEY_A, content, correlationId);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("回调id:{},消息成功消费", correlationData);
        } else {
            log.info("回调id:{},消息消费失败:{}", correlationData, cause);
        }
    }
}
*/
