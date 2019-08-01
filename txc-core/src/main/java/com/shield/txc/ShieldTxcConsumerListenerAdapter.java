package com.shield.txc;

import com.google.common.base.Preconditions;
import com.shield.txc.constant.CommonProperty;
import com.shield.txc.listener.ShieldTxcCommitListener;
import com.shield.txc.listener.ShieldTxcRollbackListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 19:24
 * @className ShieldTxcConsumerListenerAdapter
 * @desc TODO 事务消费适配器
 */
public class ShieldTxcConsumerListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldTxcConsumerListenerAdapter.class);

    private ShieldTxcCommitListener txCommmtListener;

    private ShieldTxcRollbackListener txRollbackListener;

    private String nameSrvAddr;

    private String topic = CommonProperty.DEFAULT_SHIELD_TXC_TOPIC;

    private ShieldTxcRocketMQConsumerClient shieldTxcRocketMQConsumerClient;


    public ShieldTxcConsumerListenerAdapter() {}

    public ShieldTxcConsumerListenerAdapter(String nameSrvAddr,
                                            String topic,
                                            ShieldTxcCommitListener txCommmtListener,
                                            ShieldTxcRollbackListener txRollbackListener) {
        this.nameSrvAddr = nameSrvAddr;
        this.topic = topic;
        this.txCommmtListener = txCommmtListener;
        this.txRollbackListener = txRollbackListener;
        init();
    }

    public ShieldTxcConsumerListenerAdapter init() {
        // 初始化shieldTxcRocketMQConsumerClient
        Preconditions.checkNotNull(this.nameSrvAddr, "please insert RocketMQ NameServer address");
        Preconditions.checkNotNull(this.txCommmtListener, "please initialize a ShieldTxcCommitListener instance");
        Preconditions.checkNotNull(this.txRollbackListener, "please initialize a ShieldTxcRollbackListener instance");
        shieldTxcRocketMQConsumerClient =
                new ShieldTxcRocketMQConsumerClient(this.topic, this.nameSrvAddr, this.txCommmtListener, this.txRollbackListener);
        LOGGER.debug("Initializing [ShieldTxcRocketMQConsumerClient] instance init success.");
        return this;
    }

    /**
     * 消费事务提交消息
     * @param msgs
     * @param context
     * @return
     */
    public ConsumeConcurrentlyStatus txCommit(final List<MessageExt> msgs,
                                              final ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus consumeConcurrentlyStatus;
        consumeConcurrentlyStatus = txCommmtListener.consumeMessage(msgs, context);
        return consumeConcurrentlyStatus;
    }

    /**
     * 消费事务回滚消息
     * @param msgs
     * @param context
     * @return
     */
    public ConsumeConcurrentlyStatus txRollback(final List<MessageExt> msgs,
                                                final ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus consumeConcurrentlyStatus;
        consumeConcurrentlyStatus = txRollbackListener.consumeMessage(msgs, context);
        return consumeConcurrentlyStatus;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public ShieldTxcConsumerListenerAdapter setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public ShieldTxcConsumerListenerAdapter setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public ShieldTxcCommitListener getTxCommmtListener() {
        return txCommmtListener;
    }

    public ShieldTxcConsumerListenerAdapter setTxCommmtListener(ShieldTxcCommitListener txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
        return this;
    }

    public ShieldTxcRollbackListener getTxRollbackListener() {
        return txRollbackListener;
    }

    public ShieldTxcConsumerListenerAdapter setTxRollbackListener(ShieldTxcRollbackListener txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
        return this;
    }
}
