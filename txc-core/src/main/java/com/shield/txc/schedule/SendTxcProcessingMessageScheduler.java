package com.shield.txc.schedule;

import com.shield.txc.BaseEventService;
import com.shield.txc.ShieldTxcMessage;
import com.shield.txc.ShieldTxcRocketMQProducerClient;
import com.shield.txc.constant.BizCode;
import com.shield.txc.constant.CommonProperty;
import com.shield.txc.constant.EventStatus;
import com.shield.txc.constant.TXType;
import com.shield.txc.domain.BizResult;
import com.shield.txc.domain.ShieldEvent;
import com.shield.txc.util.MessagePropertyBuilder;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 9:24
 * @className SendTxcMessageScheduler
 * @desc 发送处于处理中的消息调度线程
 */
public class SendTxcProcessingMessageScheduler extends AbstractMessageScheduler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendTxcProcessingMessageScheduler.class);

    ScheduledExecutorService executorService;

    private long initialDelay = 0;

    private long period = 10;

    private int corePoolSize = 1;

    BaseEventService baseEventService;

    ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public SendTxcProcessingMessageScheduler() {
        executorService = Executors.newScheduledThreadPool(corePoolSize);
    }

    public void schedule() {
        executorService.scheduleAtFixedRate(
                this,
                this.initialDelay,
                this.period,
                this.timeUnit);
    }

    @Override
    public void run() {
        // 查询并发送消息
        try {
            // 获取处理中的消息，初始态==处理中
            List<ShieldEvent> shieldEvents = baseEventService.queryEventListByStatus(EventStatus.PRODUCE_PROCESSING.toString());
            if (CollectionUtils.isEmpty(shieldEvents)) {
                return;
            }
            for (ShieldEvent shieldEvent : shieldEvents) {
                processBeforeSendMessage(shieldEvent);
                sendMessage(shieldEvent);
                processAfterSendMessage(shieldEvent);
            }
        } catch (Exception e) {
            LOGGER.error("Sending rollback message occurred Exception!", e);
            return;
        }
    }

    @Override
    public void processBeforeSendMessage(ShieldEvent shieldEvent) {
        LOGGER.debug("[PRODUCE_PROCESSING]Message schedule starting...id={}", shieldEvent.getId());
    }

    /**
     * 发送消息
     * @param shieldEvent
     */
    @Override
    public void sendMessage(ShieldEvent shieldEvent) {
        // 组装Message
        ShieldTxcMessage shieldTxcMessage = new ShieldTxcMessage();
        shieldTxcMessage
                .setId(String.valueOf(shieldEvent.getId()))
                .setAppId(shieldEvent.getAppId())
                .setContent(shieldEvent.getContent())
                .setEventType(shieldEvent.getEventType())
                .setEventStatus(shieldEvent.getEventStatus())
                .setTxType(shieldEvent.getTxType());

        String topic = null;
        BizResult bizResult = null;
        // 发送commit消息
        if (TXType.COMMIT.toString().equals(shieldTxcMessage.getTxType())) {
            topic = MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_COMMMIT_STAGE,
                    shieldTxcRocketMQProducerClient.getTopic());
            Message commitMessage = new Message(topic, shieldTxcMessage.encode().getBytes());
            bizResult = shieldTxcRocketMQProducerClient.sendCommitMsg(commitMessage, shieldEvent.getId());
        }
        // 发送rollback消息
        if (TXType.ROLLBACK.toString().equals(shieldTxcMessage.getTxType())) {
            topic = MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_ROLLBACK_STAGE,
                    shieldTxcRocketMQProducerClient.getTopic());
            Message rollbackMessage = new Message(topic, shieldTxcMessage.encode().getBytes());
            bizResult = shieldTxcRocketMQProducerClient.sendRollbackMsg(rollbackMessage, shieldEvent.getId());
        }
        // 判断发送结果,成功则更新为已发送
        if (bizResult.getBizCode() != BizCode.SEND_MESSAGE_SUCC) {
            return;
        }
    }

    @Override
    public void processAfterSendMessage(ShieldEvent shieldEvent) {
        // 更新前状态:生产处理中
        shieldEvent.setBeforeUpdateEventStatus(shieldEvent.getEventStatus());
        // 更新后状态:生产处理完成
        shieldEvent.setEventStatus(EventStatus.PRODUCE_PROCESSED.toString());
        boolean updateBefore = this.getBaseEventService().updateEventStatusById(shieldEvent);
        if(!updateBefore) {
            // 更新失败
            return;
        }
    }

    public String getServiceName() {
        return SendTxcProcessingMessageScheduler.class.getSimpleName();
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public long getPeriod() {
        return period;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public BaseEventService getBaseEventService() {
        return baseEventService;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public SendTxcProcessingMessageScheduler setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public SendTxcProcessingMessageScheduler setPeriod(long period) {
        this.period = period;
        return this;
    }

    public SendTxcProcessingMessageScheduler setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public SendTxcProcessingMessageScheduler setBaseEventService(BaseEventService baseEventService) {
        this.baseEventService = baseEventService;
        return this;
    }

    public SendTxcProcessingMessageScheduler setShieldTxcRocketMQProducerClient(ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient) {
        this.shieldTxcRocketMQProducerClient = shieldTxcRocketMQProducerClient;
        return this;
    }

    public SendTxcProcessingMessageScheduler setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }
}
