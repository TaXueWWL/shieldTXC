package com.shield.txc.schedule;

import com.alibaba.fastjson.JSON;
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
 * @date 2019/8/1 9:25
 * @className SendTxcMessageScheduler
 * @desc 发送事务消息调度线程
 */
public class SendTxcMessageScheduler extends AbstractMessageScheduler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendTxcMessageScheduler.class);

    ScheduledExecutorService executorService;

    private long initialDelay = 0;

    private long period = 5;

    private int corePoolSize = 1;

    BaseEventService baseEventService;

    ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public SendTxcMessageScheduler() {
        executorService = Executors.newScheduledThreadPool(corePoolSize);
    }

    public SendTxcMessageScheduler(int initialDelay,
                                   int period,
                                   int corePoolSize) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.corePoolSize = corePoolSize;
        executorService = Executors.newScheduledThreadPool(this.corePoolSize);
    }

    @Override
    public void run() {
        // 查询并发送消息
        try {
            // 获取待调度的消息，初始态==初始化
            List<ShieldEvent> shieldEvents = baseEventService.queryEventListByStatus(EventStatus.PRODUCE_INIT.toString());
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

    /**
     * 发送前改状态
     * 生产初始化->生产处理中
     * @param shieldEvent
     */
    @Override
    public void processBeforeSendMessage(ShieldEvent shieldEvent) {
        // 更新前状态:生产初始化
        shieldEvent.setBeforeUpdateEventStatus(shieldEvent.getEventStatus());
        // 更新后状态:生产处理中
        shieldEvent.setEventStatus(EventStatus.PRODUCE_PROCESSING.toString());
        boolean updateBefore = this.getBaseEventService().updateEventStatusById(shieldEvent);
        if(!updateBefore) {
            // 更新失败
            return;
        }
    }

    /**
     * 发送事务消息
     * @param shieldEvent
     */
    @Override
    public void sendMessage(ShieldEvent shieldEvent) {
        int eventId = shieldEvent.getId();
        // 组装Message
        ShieldTxcMessage shieldTxcMessage = new ShieldTxcMessage();
        shieldTxcMessage
                .setId(String.valueOf(eventId))
                .setAppId(shieldEvent.getAppId())
                .setContent(shieldEvent.getContent())
                .setEventType(shieldEvent.getEventType())
                .setEventStatus(shieldEvent.getEventStatus())
                .setTxType(shieldEvent.getTxType())
                .setBizKey(shieldEvent.getBizKey());

        String messgeBody = shieldTxcMessage.encode();
        String topic = null;
        BizResult bizResult = null;
        // 发送commit消息
        if (TXType.COMMIT.toString().equals(shieldTxcMessage.getTxType())) {
            topic = MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_COMMMIT_STAGE,
                    shieldTxcRocketMQProducerClient.getTopic());
            Message commitMessage = new Message(topic, messgeBody.getBytes());
            bizResult = shieldTxcRocketMQProducerClient.sendCommitMsg(commitMessage, eventId);
        }
        // 发送rollback消息
        if (TXType.ROLLBACK.toString().equals(shieldTxcMessage.getTxType())) {
            topic = MessagePropertyBuilder.topic(CommonProperty.TRANSACTION_ROLLBACK_STAGE,
                    shieldTxcRocketMQProducerClient.getTopic());
            Message rollbackMessage = new Message(topic, messgeBody.getBytes());
            bizResult = shieldTxcRocketMQProducerClient.sendRollbackMsg(rollbackMessage, eventId);
        }
        // TODO
        System.out.println("发送事务消息:发送结果，[0]表示成功" + JSON.toJSONString(bizResult) + "\r\n" + "发送事务消息:消息体" + messgeBody);
        // 判断发送结果,成功则更新为已发送
        if (bizResult.getBizCode() != BizCode.SEND_MESSAGE_SUCC) {
            return;
        }
    }

    /**
     * 发送后改状态
     * 生产处理中->生产处理完成
     * @param shieldEvent
     */
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

    public void schedule() {
        executorService.scheduleAtFixedRate(
                this,
                this.initialDelay,
                this.period,
                this.timeUnit);
    }

    public String getServiceName() {
        return SendTxcMessageScheduler.class.getSimpleName();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public SendTxcMessageScheduler setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public SendTxcMessageScheduler setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public long getPeriod() {
        return period;
    }

    public SendTxcMessageScheduler setPeriod(long period) {
        this.period = period;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public BaseEventService getBaseEventService() {
        return baseEventService;
    }

    public SendTxcMessageScheduler setBaseEventService(BaseEventService baseEventService) {
        this.baseEventService = baseEventService;
        return this;
    }

    public ShieldTxcRocketMQProducerClient getShieldTxcRocketMQProducerClient() {
        return shieldTxcRocketMQProducerClient;
    }

    public SendTxcMessageScheduler setShieldTxcRocketMQProducerClient(ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient) {
        this.shieldTxcRocketMQProducerClient = shieldTxcRocketMQProducerClient;
        return this;
    }
}
