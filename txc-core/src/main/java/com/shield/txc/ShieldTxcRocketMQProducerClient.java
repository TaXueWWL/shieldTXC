package com.shield.txc;

import com.shield.txc.constant.*;
import com.shield.txc.domain.AbstractShieldTxcMessage;
import com.shield.txc.domain.BizResult;
import com.shield.txc.domain.ShieldEvent;
import com.shield.txc.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:32
 * @className RocketMQEventProducerClient
 * @desc RocketMQ事件发布客户端
 * // todo 预留频率设置参数，直接给留一个线程池设置入口
 *   todo shutdownhook 包括消费client
 */
public class ShieldTxcRocketMQProducerClient implements EventPublish {


    /**事件客户端核心初始化器实现*/
    TxcRocketMQProducerImpl txcRocketMQProducerImpl;

    /**事件持久化*/
    BaseEventService baseEventService;

    private String topic;

    private String nameSrvAddr;

    private int retryTimesWhenSendFailed = 0;

    public ShieldTxcRocketMQProducerClient(String topic, String nameSrvAddr) {
        // 初始化commitProducer
        this.topic = topic;
        this.nameSrvAddr = nameSrvAddr;
        txcRocketMQProducerImpl = new TxcRocketMQProducerImpl(topic, nameSrvAddr, 0);
        // 启动内部消息发送器
        txcRocketMQProducerImpl.start();
    }


    public ShieldTxcRocketMQProducerClient(String topic, String nameSrvAddr, int retryTimesWhenSendFailed) {
        // 初始化commitProducer
        this.topic = topic;
        this.nameSrvAddr = nameSrvAddr;
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
        txcRocketMQProducerImpl = new TxcRocketMQProducerImpl(topic, nameSrvAddr, retryTimesWhenSendFailed);
        // 启动内部消息发送器
        txcRocketMQProducerImpl.start();
    }

    /**
     * commit消息持久化
     * @param shieldTxcMessage
     * @param eventType
     */
    @Transactional(rollbackFor = Exception.class)
    public void putCommitMessage(AbstractShieldTxcMessage shieldTxcMessage,
                           EventType eventType) {
        putMessage(shieldTxcMessage, eventType, TXType.COMMIT, null);
    }

    /**
     * commit消息持久化
     * @param shieldTxcMessage
     * @param eventType
     */
    @Transactional(rollbackFor = Exception.class)
    public void putRollbackMessage(AbstractShieldTxcMessage shieldTxcMessage,
                                 EventType eventType) {
        putMessage(shieldTxcMessage, eventType, TXType.ROLLBACK, null);
    }


    /**
     * commit消息持久化
     * @param shieldTxcMessage
     * @param eventType
     */
    @Transactional(rollbackFor = Exception.class)
    public void putCommitMessage(AbstractShieldTxcMessage shieldTxcMessage,
                                 EventType eventType,
                                 String appId) {
        putMessage(shieldTxcMessage, eventType, TXType.COMMIT, appId);
    }

    /**
     * commit消息持久化
     * @param shieldTxcMessage
     * @param eventType
     */
    @Transactional(rollbackFor = Exception.class)
    public void putRollbackMessage(AbstractShieldTxcMessage shieldTxcMessage,
                                   EventType eventType,
                                   String appId) {
        putMessage(shieldTxcMessage, eventType, TXType.ROLLBACK, appId);
    }

    /**
     * 消息持久化
     * @param shieldTxcMessage
     * @param eventType
     * @param txType
     */
    @Transactional(rollbackFor = Exception.class)
    public void putMessage(AbstractShieldTxcMessage shieldTxcMessage,
                           EventType eventType,
                           TXType txType) {
        putMessage(shieldTxcMessage, eventType, txType, null);
    }

    /**
     * 消息持久化
     * @param shieldTxcMessage
     * @param eventType
     * @param txType
     * @param appId
     */
    @Transactional(rollbackFor = Exception.class)
    public void putMessage(AbstractShieldTxcMessage shieldTxcMessage,
                       EventType eventType,
                       TXType txType,
                       String appId) {
        ShieldEvent event = new ShieldEvent();
        event.setEventType(eventType.toString())
                .setTxType(txType.toString())
                .setEventStatus(EventStatus.PRODUCE_INIT.toString())
                .setContent(shieldTxcMessage.encode())
                .setAppId(appId);
        try {
            // 入库失败回滚
            boolean insertResult = this.getBaseEventService().insertEvent(event);
            if (!insertResult) {
                throw new BizException("insert ShieldEvent into DB occurred Exception!");
            }
        } catch (Exception e) {
            // 异常回滚
            throw new BizException("insert ShieldEvent into DB occurred Exception!", e);
        }
    }

    /**
     * Send Commit message
     */
    public BizResult sendCommitMsg(Message msg, long timeout, int eventId) {
        try {
            msg.setTopic(msg.getTopic());
            SendResult sendResult = this.getCommitProducer().send(msg, timeout);
            return processAfterSendMsg(sendResult, eventId);
        } catch (Exception e) {
            return BizResult.bizResult(BizCode.SEND_MESSAGE_FAIL);
        }
    }

    /**
     * Send Commit message
     */
    public BizResult sendCommitMsg(Message msg, int eventId) {
        try {
            msg.setTopic(msg.getTopic());
            SendResult sendResult = this.getCommitProducer().send(msg);
            return processAfterSendMsg(sendResult, eventId);
        } catch (Exception e) {
            return BizResult.bizResult(BizCode.SEND_MESSAGE_FAIL);
        }
    }


    /**
     * Send rollback message
     */
    public BizResult sendRollbackMsg(Message msg, long timeout, int eventId) {
        try {
            msg.setTopic(msg.getTopic());
            SendResult sendResult = this.getRollbackProducer().send(msg, timeout);
            return processAfterSendMsg(sendResult, eventId);
        } catch (Exception e) {
            return BizResult.bizResult(BizCode.SEND_MESSAGE_FAIL);
        }
    }

    /**
     * Send rollback message
     */
    public BizResult sendRollbackMsg(Message msg, int eventId) {
        try {
            msg.setTopic(msg.getTopic());
            SendResult sendResult = this.getRollbackProducer().send(msg);
            return processAfterSendMsg(sendResult, eventId);
        } catch (Exception e) {
            return BizResult.bizResult(BizCode.SEND_MESSAGE_FAIL);
        }
    }

    /**
     * 发送消息后处理逻辑
     * @param sendResult
     * @return
     */
    private BizResult processAfterSendMsg(SendResult sendResult, int eventId) {
        if (sendResult == null) {
            return BizResult.bizResult(BizCode.SEND_MESSAGE_FAIL);
        }
        String msgId = sendResult.getMsgId();
        if (StringUtils.isBlank(msgId)) {
            return BizResult.bizResult(BizCode.SEND_MESSAGE_FAIL);
        }
        // 更新消息状态:处理中->完成
        ShieldEvent event = new ShieldEvent();
        event.setId(eventId)
                .setEventStatus(EventStatus.PRODUCE_PROCESSED.toString())
                .setBeforeUpdateEventStatus(EventStatus.PRODUCE_PROCESSING.toString());
        this.getBaseEventService().updateEventStatusById(event);
        return BizResult.bizResult(BizCode.SEND_MESSAGE_SUCC);
    }


    @Override
    public void init() {}

    @Override
    public void start() {
        this.txcRocketMQProducerImpl.start();
    }

    @Override
    public void shutdown() {
        this.txcRocketMQProducerImpl.shutdown();
    }

    @Override
    public MessageQueueType messageQueueType() {
        return MessageQueueType.ROCKETMQ;
    }

    public DefaultMQProducer getCommitProducer() {
        return this.txcRocketMQProducerImpl.getCommitProducer();
    }

    public DefaultMQProducer getRollbackProducer() {
        return this.txcRocketMQProducerImpl.getRollbackProducer();
    }

    public String getTopic() {
        return topic;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }


    public BaseEventService getBaseEventService() {
        return baseEventService;
    }

    public ShieldTxcRocketMQProducerClient setBaseEventService(BaseEventService baseEventService) {
        this.baseEventService = baseEventService;
        return this;
    }
}
