package com.shield.txc.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 15:50
 * @className ShieldTxcRollbackListener
 * @desc
 */
public class ShieldTxcRollbackListener implements MessageListenerConcurrently {

    private MessageListenerConcurrently txRollbackListener;

    public ShieldTxcRollbackListener(MessageListenerConcurrently txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
    }

    public ShieldTxcRollbackListener() {
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        return this.txRollbackListener.consumeMessage(msgs, context);
    }

    public MessageListenerConcurrently getTxRollbackListener() {
        return txRollbackListener;
    }

    public ShieldTxcRollbackListener setTxRollbackListener(MessageListenerConcurrently txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
        return this;
    }
}
