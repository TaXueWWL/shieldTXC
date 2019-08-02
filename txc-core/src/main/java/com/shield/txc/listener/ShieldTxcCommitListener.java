package com.shield.txc.listener;

import com.alibaba.fastjson.JSON;
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
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 15:50
 * @className ShieldTxcCommitListener
 * @desc shieldTXC事务提交消息监听器
 */
public class ShieldTxcCommitListener implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldTxcCommitListener.class);

    public ShieldTxcCommitListener(MessageListenerConcurrently txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
    }

    public ShieldTxcCommitListener() {
    }

    private MessageListenerConcurrently txCommmtListener;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

        for (MessageExt msg : msgs) {
            String msgBody = new String(msg.getBody());
            String msgId = msg.getMsgId();
            System.out.println("commit消息----" + msgBody);

            ShieldTxcMessage shieldTxcMessage = new ShieldTxcMessage();
            shieldTxcMessage.decode(msgBody);

            ShieldEvent event = new ShieldEvent();
            event.convert(shieldTxcMessage);

            BaseEventService baseEventService =
                    (BaseEventService) SpringApplicationHolder.getBean("baseEventService");
            try {
                // 消费幂等,查询消息是否存在，入库带唯一索引
                // 消费次数大于等于阈值，回滚事务
                int currReconsumeTimes = msg.getReconsumeTimes();
                if (currReconsumeTimes >= CommonProperty.MAX_COMMIT_RECONSUME_TIMES) {
                    // 事务回滚操作，消息复制为回滚生产者，持久化
                    LOGGER.debug("[ShieldTxcCommitListener] START transaction rollback sequence! msgId={}", msgId);
                    if (doPutRollbackMsgAfterMaxConsumeTimes(baseEventService, event, msgId)) {
                        LOGGER.debug("[ShieldTxcCommitListener] transaction rollback sequence executed SUCCESS! msgId={}", msgId);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else {
                        // 如果一直失败最后会进死信
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }

                String bizKey = shieldTxcMessage.getBizKey();
                String txType = shieldTxcMessage.getTxType();
                String eventStatua = shieldTxcMessage.getEventStatus();
                String appId = shieldTxcMessage.getAppId();
                String eventType = shieldTxcMessage.getEventType();

                // 进行消息持久化
                event.setEventType(eventType)
                        .setTxType(txType)
                        .setEventStatus(EventStatus.CONSUME_INIT.toString())
                        .setContent(shieldTxcMessage.getContent())
                        .setAppId(shieldTxcMessage.getAppId())
                        .setBizKey(bizKey)
                        .setId(Integer.valueOf(shieldTxcMessage.getId()));
                // 入库失败回滚
                boolean insertResult = baseEventService.insertEventWithId(event);
                if (!insertResult) {
                    LOGGER.warn("[ShieldTxcCommitListener] insert shieldEvent Consume Message failed,msgId={}", msgId);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                // 改消费处理中
                doUpdateMessageStatusProcessing(baseEventService, event);
                // 真实消费
                return doUpdateAfterConsumed(baseEventService, this.txCommmtListener.consumeMessage(msgs, context), event);
            } catch (Exception e) {
                // 幂等处理：唯一约束触发则直接进行消费
                if (e.getMessage() != null && e.getMessage().indexOf(CommonProperty.MESSAGE_HAS_EXISTED_INDEX) >= 0) {
                    LOGGER.debug("[ShieldTxcCommitListener::UNIQUE INDEX], message has existed,msgId={}", msgId);
                    return doUpdateAfterConsumed(baseEventService, this.txCommmtListener.consumeMessage(msgs, context), event);
                }
                if (e.getMessage() != null && e.getMessage().indexOf(CommonProperty.MESSAGE_PRIMARY_KEY_DUPLICATE) >= 0) {
                    LOGGER.debug("[ShieldTxcCommitListener::Duplicate entry for key 'PRIMARY'], message has existed,msgId={}", msgId);
                    return doUpdateAfterConsumed(baseEventService, this.txCommmtListener.consumeMessage(msgs, context), event);
                }
                // 其他异常重试
                LOGGER.warn("ShieldTxcCommitListener Consume Message occurred Exception,msgId={}", msgId, e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return null;
    }

    /**
     * 消息更新为达到重试上限，插入回滚消息
     * 如果这步骤操作失败，那么就进死信
     *
     * @param baseEventService
     * @param shieldEvent
     */
    private boolean doPutRollbackMsgAfterMaxConsumeTimes(BaseEventService baseEventService,
                                                         ShieldEvent shieldEvent,
                                                         String msgId) {

        ShieldEvent queryEvent = baseEventService.queryEventById(shieldEvent.getId());
        if (!queryEvent.getEventStatus().equals(EventStatus.CONSUME_PROCESSING.toString())) {
            return false;
        }
        shieldEvent.setId(shieldEvent.getId());
        // 消费成功，处理中改完成，更新前状态:消费处理中
        shieldEvent.setBeforeUpdateEventStatus(EventStatus.CONSUME_PROCESSING.toString());
        // 更新后状态:达到最大重试次数
        shieldEvent.setEventStatus(EventStatus.CONSUME_MAX_RECONSUMETIMES.toString());
        boolean updateBefore = baseEventService.updateEventStatusById(shieldEvent);
        LOGGER.debug("[UPDATE TO CONSUME_MAX_RECONSUMETIMES] {},msgId={}", updateBefore, msgId);
        System.out.println("更新结果:" + updateBefore + "---更新事件：" + JSON.toJSONString(shieldEvent));
        if (!updateBefore) {
            // 更新失败,幂等重试.此时必定是系统依赖组件出问题了
            return false;
        }

        // 插入回滚消息
        ShieldEvent rollbackEvent = new ShieldEvent();
        BeanUtils.copyProperties(shieldEvent, rollbackEvent);
        rollbackEvent.setEventStatus(EventStatus.PRODUCE_INIT.toString())
                .setTxType(TXType.ROLLBACK.toString());
        boolean insertResult = baseEventService.insertEvent(rollbackEvent);
        LOGGER.debug("[INSERT ROLLBACK MESSAGE] {},msgId={}", insertResult, msgId);
        if (!insertResult) {
            return false;
        }
        return true;
    }

    /**
     * 拦截真实消费结果，根据消费结果更新消息状态
     *
     * @param consumeConcurrentlyStatus
     * @param baseEventService
     * @param shieldEvent
     * @return
     */
    private ConsumeConcurrentlyStatus doUpdateAfterConsumed(BaseEventService baseEventService,
                                                            ConsumeConcurrentlyStatus consumeConcurrentlyStatus,
                                                            ShieldEvent shieldEvent) {
        if (ConsumeConcurrentlyStatus.RECONSUME_LATER == consumeConcurrentlyStatus) {
            // 消费失败，消费状态仍旧处理中
            return consumeConcurrentlyStatus;
        }
        if (ConsumeConcurrentlyStatus.CONSUME_SUCCESS == consumeConcurrentlyStatus) {
            // 消费成功，处理中改完成，更新前状态:消费处理中
            shieldEvent.setBeforeUpdateEventStatus(shieldEvent.getEventStatus());
            // 更新后状态:消费处理中
            shieldEvent.setEventStatus(EventStatus.CONSUME_PROCESSED.toString());
            boolean updateBefore = baseEventService.updateEventStatusById(shieldEvent);
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
     * @param shieldEvent
     */
    private void doUpdateMessageStatusProcessing(BaseEventService baseEventService, ShieldEvent shieldEvent) {
        // 更新前状态:消费初始化
        shieldEvent.setBeforeUpdateEventStatus(shieldEvent.getEventStatus());
        // 更新后状态:消费处理中
        shieldEvent.setEventStatus(EventStatus.CONSUME_PROCESSING.toString());
        boolean updateBefore = baseEventService.updateEventStatusById(shieldEvent);
        if (!updateBefore) {
            // 更新失败
            return;
        }
    }

    public MessageListenerConcurrently getTxCommmtListener() {
        return txCommmtListener;
    }

    public ShieldTxcCommitListener setTxCommmtListener(MessageListenerConcurrently txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
        return this;
    }
}
