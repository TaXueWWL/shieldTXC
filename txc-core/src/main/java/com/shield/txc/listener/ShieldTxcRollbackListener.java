package com.shield.txc.listener;

import com.shield.txc.BaseEventService;
import com.shield.txc.ShieldTxcMessage;
import com.shield.txc.constant.CommonProperty;
import com.shield.txc.constant.EventStatus;
import com.shield.txc.constant.TXType;
import com.shield.txc.domain.ShieldEvent;
import com.shield.txc.util.SpringApplicationHolder;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 15:50
 * @className ShieldTxcRollbackListener
 * @desc shieldTXC事务回滚消息监听器
 * 回滚逻辑会一直重试到进死信。不会做额外的拦截操作
 */
public class ShieldTxcRollbackListener implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldTxcRollbackListener.class);

    private MessageListenerConcurrently txRollbackListener;

    public ShieldTxcRollbackListener(MessageListenerConcurrently txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
    }

    public ShieldTxcRollbackListener() {
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        // 测试打印消息体

        for (MessageExt msg : msgs) {

            String msgBody = new String(msg.getBody());
            String msgId = msg.getMsgId();
            System.out.println("Rollback消息----" + msgBody);

            ShieldTxcMessage shieldTxcMessage = new ShieldTxcMessage();
            shieldTxcMessage.decode(msgBody);

            ShieldEvent rollbackEvent = new ShieldEvent();
            rollbackEvent.convert(shieldTxcMessage);

            BaseEventService baseEventService =
                    (BaseEventService) SpringApplicationHolder.getBean("baseEventService");

            try {
                // 取参数
                String bizKey = shieldTxcMessage.getBizKey();
                String txType = shieldTxcMessage.getTxType();
                String eventStatua = shieldTxcMessage.getEventStatus();
                String appId = shieldTxcMessage.getAppId();
                String eventType = shieldTxcMessage.getEventType();

                // 回滚消息持久化
                rollbackEvent.setEventType(eventType)
                        .setTxType(TXType.ROLLBACK.toString())
                        .setEventStatus(EventStatus.CONSUME_INIT.toString())
                        .setContent(shieldTxcMessage.getContent())
                        .setAppId(shieldTxcMessage.getAppId())
                        .setBizKey(bizKey)
                        .setId(Integer.valueOf(shieldTxcMessage.getId()));

                // 入库失败回滚
                boolean insertResult = baseEventService.insertEventWithId(rollbackEvent);
                if (!insertResult) {
                    LOGGER.warn("[ShieldTxcRollbackListener] insert RollbackShieldEvent Consume Message failed,msgId={}", msgId);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                // 改消费处理中
                doUpdateMessageStatusProcessing(baseEventService, rollbackEvent);
                // 真实消费
                return doUpdateAfterRollbackConsumed(baseEventService, this.txRollbackListener.consumeMessage(msgs, context), rollbackEvent);
            } catch (Exception e) {
                // 幂等处理：唯一约束触发则直接进行消费
                if (e.getMessage() != null && e.getMessage().indexOf(CommonProperty.MESSAGE_HAS_EXISTED_INDEX) >= 0) {
                    LOGGER.debug("[ShieldTxcRollbackListener::UNIQUE INDEX], message has existed,msgId={}", msgId);
                    return doUpdateAfterRollbackConsumed(baseEventService, this.txRollbackListener.consumeMessage(msgs, context), rollbackEvent);
                }
                if (e.getMessage() != null && e.getMessage().indexOf(CommonProperty.MESSAGE_PRIMARY_KEY_DUPLICATE) >= 0) {
                    LOGGER.debug("[ShieldTxcRollbackListener::Duplicate entry for key 'PRIMARY'], message has existed,msgId={}", msgId);
                    return doUpdateAfterRollbackConsumed(baseEventService, this.txRollbackListener.consumeMessage(msgs, context), rollbackEvent);
                }
                // 其他异常重试
                LOGGER.warn("ShieldTxcRollbackListener Consume Message occurred Exception,msgId={}", msgId, e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return null;
    }

    /**
     * 拦截真实消费结果，根据消费结果更新消息状态
     *
     * @param consumeConcurrentlyStatus
     * @param baseEventService
     * @param rollbackEvent
     * @return
     */
    private ConsumeConcurrentlyStatus doUpdateAfterRollbackConsumed(BaseEventService baseEventService,
                                                                    ConsumeConcurrentlyStatus consumeConcurrentlyStatus,
                                                                    ShieldEvent rollbackEvent) {
        if (ConsumeConcurrentlyStatus.RECONSUME_LATER == consumeConcurrentlyStatus) {
            // 消费失败，消费状态仍旧处理中
            return consumeConcurrentlyStatus;
        }
        if (ConsumeConcurrentlyStatus.CONSUME_SUCCESS == consumeConcurrentlyStatus) {
            // 消费成功，处理中改完成，更新前状态:消费处理中
            rollbackEvent.setBeforeUpdateEventStatus(rollbackEvent.getEventStatus());
            // 更新后状态:消费处理中
            rollbackEvent.setEventStatus(EventStatus.CONSUME_PROCESSED.toString());
            boolean updateBefore = baseEventService.updateEventStatusById(rollbackEvent);
            if (!updateBefore) {
                // 更新失败,幂等重试.此时必定是系统依赖组件出问题了
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 消息改处理中
     *
     * @param baseEventService
     * @param rollbackEvent
     */
    private void doUpdateMessageStatusProcessing(BaseEventService baseEventService, ShieldEvent rollbackEvent) {
        // 更新前状态:消费初始化
        rollbackEvent.setBeforeUpdateEventStatus(rollbackEvent.getEventStatus());
        // 更新后状态:消费处理中
        rollbackEvent.setEventStatus(EventStatus.CONSUME_PROCESSING.toString());
        boolean updateBefore = baseEventService.updateEventStatusById(rollbackEvent);
        if (!updateBefore) {
            // 更新失败
            return;
        }
    }

    public MessageListenerConcurrently getTxRollbackListener() {
        return txRollbackListener;
    }

    public ShieldTxcRollbackListener setTxRollbackListener(MessageListenerConcurrently txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
        return this;
    }
}
