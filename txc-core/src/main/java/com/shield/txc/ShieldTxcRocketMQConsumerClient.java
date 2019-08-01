package com.shield.txc;

import com.shield.txc.constant.CommonProperty;
import com.shield.txc.constant.MessageQueueType;
import com.shield.txc.util.MessagePropertyBuilder;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 17:44
 * @className RocketMQEventConsumerClient
 * @desc
 */
public class ShieldTxcRocketMQConsumerClient implements EventSubscribe {



    private DefaultMQPushConsumer commmitConsumer;

    private DefaultMQPushConsumer rollbackConsumer;

    private MessageListenerConcurrently txCommmtListener;

    private MessageListenerConcurrently txRollbackListener;

    private String nameSrvAddr;

    private String topic;

    public ShieldTxcRocketMQConsumerClient(String topic,
                                           String nameSrvAddr,
                                           MessageListenerConcurrently txCommmtListener,
                                           MessageListenerConcurrently txRollbackListener) {
        this.nameSrvAddr = nameSrvAddr;
        this.topic = topic;
        this.txCommmtListener = txCommmtListener;
        this.txRollbackListener = txRollbackListener;
        // 初始化事务提交消费者
        initCommitConsumer(this.topic, this.nameSrvAddr);
        // 初始化事务回滚消费者
        initRollbackConsumer(this.topic, this.nameSrvAddr);
    }

    /**
     * 初始化事务提交消费者
     * @param topic
     * @param nameSrvAddr
     */
    private void initCommitConsumer(String topic, String nameSrvAddr) {
        commmitConsumer =
                new DefaultMQPushConsumer(
                        MessagePropertyBuilder.groupId(CommonProperty.TRANSACTION_COMMMIT_STAGE, topic));
        commmitConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        commmitConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        commmitConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        commmitConsumer.registerMessageListener(this.txCommmtListener);
        // 订阅所有消息
        try {
            commmitConsumer.subscribe(
                    MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_COMMMIT_STAGE, topic), "*");
            // 启动消费者
            commmitConsumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException("Loading [com.shield.txc.RocketMQEventConsumerClient.commmitConsumer] occurred exception", e);
        }
    }

    /**
     * 初始化事务回滚消费者
     * @param topic
     * @param nameSrvAddr
     */
    private void initRollbackConsumer(String topic, String nameSrvAddr) {
        rollbackConsumer =
                new DefaultMQPushConsumer(
                        MessagePropertyBuilder.groupId(CommonProperty.TRANSACTION_ROLLBACK_STAGE, topic));
        rollbackConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        rollbackConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        rollbackConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        rollbackConsumer.registerMessageListener(this.txRollbackListener);
        // 订阅所有消息
        try {
            rollbackConsumer.subscribe(
                    MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_ROLLBACK_STAGE, topic), "*");
            // 启动消费者
            rollbackConsumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException("Loading [com.shield.txc.RocketMQEventConsumerClient.rollbackConsumer] occurred exception", e);
        }
    }


    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {
        this.commmitConsumer.shutdown();
        this.rollbackConsumer.shutdown();
    }

    @Override
    public MessageQueueType messageQueueType() {
        return MessageQueueType.ROCKETMQ;
    }
}
