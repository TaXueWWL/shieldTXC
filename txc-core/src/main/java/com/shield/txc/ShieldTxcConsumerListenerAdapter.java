package com.shield.txc;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 19:24
 * @className ShieldTxcConsumerListenerAdapter
 * @desc TODO 事务消费适配器
 */
public class ShieldTxcConsumerListenerAdapter {

    private MessageListenerConcurrently txCommmtListener;

    private MessageListenerConcurrently txRollbackListener;

    public ShieldTxcConsumerListenerAdapter() {}

    public ShieldTxcConsumerListenerAdapter(MessageListenerConcurrently txCommmtListener,
                                            MessageListenerConcurrently txRollbackListener) {
        this.txCommmtListener = txCommmtListener;
        this.txRollbackListener = txRollbackListener;
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

    public MessageListenerConcurrently getTxCommmtListener() {
        return txCommmtListener;
    }

    public ShieldTxcConsumerListenerAdapter setTxCommmtListener(MessageListenerConcurrently txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
        return this;
    }

    public MessageListenerConcurrently getTxRollbackListener() {
        return txRollbackListener;
    }

    public ShieldTxcConsumerListenerAdapter setTxRollbackListener(MessageListenerConcurrently txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
        return this;
    }
}
