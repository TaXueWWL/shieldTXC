package com.shield.txc;

import com.shield.txc.constant.CommonProperty;
import com.shield.txc.constant.MessageQueueType;
import com.shield.txc.exception.BizException;
import com.shield.txc.listener.ShieldTxcCommitListener;
import com.shield.txc.listener.ShieldTxcRollbackListener;
import com.shield.txc.util.MessagePropertyBuilder;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 17:44
 * @className RocketMQEventConsumerClient
 * @desc
 */
public class ShieldTxcRocketMQConsumerClient implements EventSubscribe {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldTxcRocketMQConsumerClient.class);

    /**事务提交消费者*/
    private DefaultMQPushConsumer commitConsumer;
    /**事务回滚消费者*/
    private DefaultMQPushConsumer rollbackConsumer;

    private String nameSrvAddr;

    private String topic;

    public ShieldTxcRocketMQConsumerClient(String topic,
                                           String nameSrvAddr,
                                           ShieldTxcCommitListener txCommtListener,
                                           ShieldTxcRollbackListener txRollbackListener) {
        this.nameSrvAddr = nameSrvAddr;
        this.topic = topic;
        if (txCommtListener == null && txRollbackListener == null) {
            throw new BizException("Please define at least one MessageListenerConcurrently instance, such as [ShieldTxcCommitListener] or [ShieldTxcRollbackListener] or both.");
        }
        if (txCommtListener != null) {
            // 初始化事务提交消费者
            initCommitConsumer(this.topic, this.nameSrvAddr, txCommtListener);
            LOGGER.debug("Initializing [ShieldTxcRocketMQConsumerClient.CommmitConsumer] instance init success.");
        }
        if (txRollbackListener != null) {
            // 初始化事务回滚消费者
            initRollbackConsumer(this.topic, this.nameSrvAddr, txRollbackListener);
            LOGGER.debug("Initializing [ShieldTxcRocketMQConsumerClient.RollbackListener] instance init success.");
        }
    }

    /**
     * 初始化事务提交消费者
     * @param topic
     * @param nameSrvAddr
     */
    private void initCommitConsumer(String topic, String nameSrvAddr, ShieldTxcCommitListener txCommtListener) {
        commitConsumer =
                new DefaultMQPushConsumer(
                        MessagePropertyBuilder.groupId(CommonProperty.TRANSACTION_COMMMIT_STAGE, topic));
        commitConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        commitConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        commitConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        commitConsumer.registerMessageListener(txCommtListener);
        // 订阅所有消息
        try {
            commitConsumer.subscribe(
                    MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_COMMMIT_STAGE, topic), "*");
            // 启动消费者
            commitConsumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException("Loading [com.shield.txc.RocketMQEventConsumerClient.commmitConsumer] occurred exception", e);
        }
    }

    /**
     * 初始化事务回滚消费者
     * @param topic
     * @param nameSrvAddr
     */
    private void initRollbackConsumer(String topic, String nameSrvAddr, ShieldTxcRollbackListener txRollbackListener) {
        rollbackConsumer =
                new DefaultMQPushConsumer(
                        MessagePropertyBuilder.groupId(CommonProperty.TRANSACTION_ROLLBACK_STAGE, topic));
        rollbackConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        rollbackConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        rollbackConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        rollbackConsumer.registerMessageListener(txRollbackListener);
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
        this.commitConsumer.shutdown();
        this.rollbackConsumer.shutdown();
    }

    @Override
    public MessageQueueType messageQueueType() {
        return MessageQueueType.ROCKETMQ;
    }
}
