package com.shield.txc;

import com.google.common.base.Preconditions;
import com.shield.txc.constant.CommonProperty;
import com.shield.txc.constant.MessageQueueType;
import com.shield.txc.util.MessagePropertyBuilder;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 22:15
 * @className TxcRocketMQProducerImpl
 * @desc
 */
class TxcRocketMQProducerImpl implements EventPublish {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxcRocketMQProducerImpl.class);

    // 事务提交生产者
    private DefaultMQProducer commitProducer;

    // 事务回滚生产者
    private DefaultMQProducer rollbackProducer;

    private int retryTimesWhenSendFailed = 0;

    public TxcRocketMQProducerImpl() {}

    public TxcRocketMQProducerImpl(String topic, String nameSrvAddr, int retryTimesWhenSendFailed) {
        commitProducer = new DefaultMQProducer();
        rollbackProducer = new DefaultMQProducer();
        initCommit(topic, nameSrvAddr, retryTimesWhenSendFailed);
        initRollback(topic, nameSrvAddr, retryTimesWhenSendFailed);
    }

    /**
     * 构造commit生产者
     * @param topic
     * @param nameSrvAddr
     * @return
     */
    public TxcRocketMQProducerImpl initCommit(String topic,
                                              String nameSrvAddr) {
        if (this.getRetryTimesWhenSendFailed() > 0) {
            return this.initCommit(topic, nameSrvAddr, this.getRetryTimesWhenSendFailed());
        }
        return this.initCommit(topic, nameSrvAddr, 0);
    }

    /**
     * 初始化事件提交发布客户端
     * @param topic
     * @param nameSrvAddr
     * @param retryTimesWhenSendFailed
     * @return
     */
    TxcRocketMQProducerImpl initCommit(String topic, String nameSrvAddr, int retryTimesWhenSendFailed) {
        LOGGER.debug("Initializing [TxcRocketMQProducerImpl.commitProducer] instance init success.");
        return this.init(this.commitProducer, topic, nameSrvAddr, CommonProperty.TRANSACTION_COMMMIT_STAGE, retryTimesWhenSendFailed);
    }



    /**
     * 构造commit生产者
     * @param topic
     * @param nameSrvAddr
     * @return
     */
    public TxcRocketMQProducerImpl initRollback(String topic, String nameSrvAddr) {
        LOGGER.debug("Initializing [TxcRocketMQProducerImpl.initRollback] instance init success.");
        if (this.getRetryTimesWhenSendFailed() > 0) {
            return this.initCommit(topic, nameSrvAddr, this.getRetryTimesWhenSendFailed());
        }
        return this.initCommit(topic, nameSrvAddr, 0);
    }


    /**
     * 初始化事件回滚发布客户端
     * @param topic
     * @param nameSrvAddr
     * @param retryTimesWhenSendFailed
     * @return
     */
    TxcRocketMQProducerImpl initRollback(String topic, String nameSrvAddr, int retryTimesWhenSendFailed) {
        return this.init(this.getRollbackProducer(), topic, nameSrvAddr, CommonProperty.TRANSACTION_ROLLBACK_STAGE, retryTimesWhenSendFailed);
    }


    TxcRocketMQProducerImpl init(DefaultMQProducer defaultMQProducer,
                                         String topic,
                                         String nameSrvAddr,
                                         String tranStage,
                                         int retryTimesWhenSendFailed) {
        Preconditions.checkNotNull(topic, "please insert event publish topic");
        Preconditions.checkNotNull(nameSrvAddr, "please insert RocketMQ NameServer address");
        Preconditions.checkNotNull(tranStage, "please insert correct tranStage from com.snowalker.txc.constant.CommonProperty");
        String producerGroup = MessagePropertyBuilder.groupId(tranStage, topic);
        defaultMQProducer.setProducerGroup(producerGroup);
        defaultMQProducer.setNamesrvAddr(nameSrvAddr);
        defaultMQProducer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        return this;
    }

    public DefaultMQProducer getCommitProducer() {
        return commitProducer;
    }

    public TxcRocketMQProducerImpl setCommitProducer(DefaultMQProducer commitProducer) {
        this.commitProducer = commitProducer;
        return this;
    }

    public DefaultMQProducer getRollbackProducer() {
        return rollbackProducer;
    }

    public TxcRocketMQProducerImpl setRollbackProducer(DefaultMQProducer rollbackProducer) {
        this.rollbackProducer = rollbackProducer;
        return this;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public TxcRocketMQProducerImpl setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
        return this;
    }

    @Override
    public MessageQueueType messageQueueType() {
        return MessageQueueType.ROCKETMQ;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {
        try {
            this.commitProducer.start();
            this.rollbackProducer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        this.commitProducer.shutdown();
        this.rollbackProducer.shutdown();
    }
}
