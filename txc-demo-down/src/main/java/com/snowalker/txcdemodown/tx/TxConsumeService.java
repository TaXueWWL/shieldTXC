package com.snowalker.txcdemodown.tx;

import com.shield.txc.ShieldTxcConsumerListenerAdapter;
import com.shield.txc.listener.ShieldTxcCommitListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 15:26
 * @className TxConsumeService
 * @desc TODO tag支持
 * 事务下游只需要订阅事务消费监听，ShieldTxcCommitListener
 */
@Service
public class TxConsumeService implements InitializingBean {


    @Value("${shield.event.rocketmq.nameSrvAddr}")
    String nameSerAddr;

    @Value("${shield.event.rocketmq.topicSource}")
    String topic;

    @Override
    public void afterPropertiesSet() throws Exception {

        new ShieldTxcConsumerListenerAdapter(nameSerAddr, topic, new ShieldTxcCommitListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("测试消费ShieldTxcCommitListener开始......");

                Random ra =new Random();
                int randomInt = ra.nextInt(10) + 1;
                if (randomInt <= 5) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } else {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        }));

    }
}
