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
 * @className ShieldTxcCommitListener
 * @desc
 */
public class ShieldTxcCommitListener implements MessageListenerConcurrently {

    public ShieldTxcCommitListener(MessageListenerConcurrently txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
    }

    public ShieldTxcCommitListener() {
    }

    private MessageListenerConcurrently txCommmtListener;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        return this.txCommmtListener.consumeMessage(msgs, context);
    }

    public MessageListenerConcurrently getTxCommmtListener() {
        return txCommmtListener;
    }

    public ShieldTxcCommitListener setTxCommmtListener(MessageListenerConcurrently txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
        return this;
    }
}
