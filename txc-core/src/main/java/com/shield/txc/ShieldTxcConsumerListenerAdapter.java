package com.shield.txc;

import com.google.common.base.Preconditions;
import com.shield.txc.constant.CommonProperty;
import com.shield.txc.listener.ShieldTxcCommitListener;
import com.shield.txc.listener.ShieldTxcRollbackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 19:24
 * @className ShieldTxcConsumerListenerAdapter
 * @desc 事务消费适配器
 */
public class ShieldTxcConsumerListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldTxcConsumerListenerAdapter.class);

    /**shieldTXC事务提交消息监听器,事务下游必须实现*/
    private ShieldTxcCommitListener txCommmtListener;

    /**shieldTXC事务回滚消息监听器,事务上游必须实现*/
    private ShieldTxcRollbackListener txRollbackListener;

    private String nameSrvAddr;

    private String topic = CommonProperty.DEFAULT_SHIELD_TXC_TOPIC;

    private ShieldTxcRocketMQConsumerClient shieldTxcRocketMQConsumerClient;


    public ShieldTxcConsumerListenerAdapter() {}


    /**
     * 事务下游可选
     * @param nameSrvAddr
     * @param topic
     * @param txCommmtListener
     */
    public ShieldTxcConsumerListenerAdapter(String nameSrvAddr,
                                            String topic,
                                            ShieldTxcCommitListener txCommmtListener) {
        this.nameSrvAddr = nameSrvAddr;
        this.topic = topic;
        this.txCommmtListener = txCommmtListener;
        init();
    }


    /**
     * 事务上游可选
     * @param nameSrvAddr
     * @param topic
     * @param txRollbackListener
     */
    public ShieldTxcConsumerListenerAdapter(String nameSrvAddr,
                                            String topic,
                                            ShieldTxcRollbackListener txRollbackListener) {
        this.nameSrvAddr = nameSrvAddr;
        this.topic = topic;
        this.txRollbackListener = txRollbackListener;
        init();
    }

    public ShieldTxcConsumerListenerAdapter(String nameSrvAddr,
                                            String topic,
                                            ShieldTxcCommitListener txCommmtListener,
                                            ShieldTxcRollbackListener txRollbackListener) {
        this.nameSrvAddr = nameSrvAddr;
        this.topic = topic;
        this.txCommmtListener = txCommmtListener;
        this.txRollbackListener = txRollbackListener;
        init();
    }

    public ShieldTxcConsumerListenerAdapter init() {
        // 初始化shieldTxcRocketMQConsumerClient
        Preconditions.checkNotNull(this.nameSrvAddr, "please insert RocketMQ NameServer address");
        shieldTxcRocketMQConsumerClient =
                new ShieldTxcRocketMQConsumerClient(this.topic, this.nameSrvAddr, this.getTxCommmtListener(), this.getTxRollbackListener());
        LOGGER.debug("Initializing [ShieldTxcRocketMQConsumerClient] instance init success.");
        return this;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public ShieldTxcConsumerListenerAdapter setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public ShieldTxcConsumerListenerAdapter setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public ShieldTxcCommitListener getTxCommmtListener() {
        return txCommmtListener;
    }

    public ShieldTxcConsumerListenerAdapter setTxCommmtListener(ShieldTxcCommitListener txCommmtListener) {
        this.txCommmtListener = txCommmtListener;
        return this;
    }

    public ShieldTxcRollbackListener getTxRollbackListener() {
        return txRollbackListener;
    }

    public ShieldTxcConsumerListenerAdapter setTxRollbackListener(ShieldTxcRollbackListener txRollbackListener) {
        this.txRollbackListener = txRollbackListener;
        return this;
    }
}
