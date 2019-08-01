package com.shield.txc.listener;

import com.shield.txc.ShieldTxcMessage;
import com.shield.txc.constant.CommonProperty;
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
 * @desc shieldTXC事务回滚消息监听器
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
        // 测试打印消息体
        try {
            for (MessageExt msg : msgs) {
                String msgBody = new String(msg.getBody());
                // 消费次数大于等于阈值，回滚事务
                int currReconsumeTimes = msg.getReconsumeTimes();
                if (currReconsumeTimes >= CommonProperty.MAX_COMMIT_RECONSUME_TIMES) {
                    // TODO 事务回滚操作
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                // 消费幂等,查询消息是否存在
                ShieldTxcMessage shieldTxcMessage = new ShieldTxcMessage();
                shieldTxcMessage.decode(msgBody);
                // 消息入库
                System.out.println("接收到回滚消息:" + msgBody);
            }
        } catch (Exception e) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
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
