package com.shield.txc.configuration;

import com.shield.txc.constant.CommonProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 16:58
 * @className RocketMQProperties
 * @desc
 */
@ConfigurationProperties(prefix = "shield.event.rocketmq")
public class RocketMQProperties {

    /**
     * nameServer地址
     */
    private String nameSrvAddr;
    /**
     * 消息主题源
     */
    private String topicSource = CommonProperty.DEFAULT_SHIELD_TXC_TOPIC;
    /**
     * 消息发送重试次数
     */
    private int retryTimesWhenSendFailed = 0;

    /**
     * 异步消息投递：调度初始延时
     */
    private int tranMessageSendInitialDelay = 0;

    /**
     * 异步消息投递：调度延时
     */
    private long tranMessageSendPeriod = 5;

    /**
     * 异步消息投递：调度线程核心数量
     */
    private int tranMessageSendCorePoolSize = 1;

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public RocketMQProperties setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
        return this;
    }

    public String getTopicSource() {
        return topicSource;
    }

    public RocketMQProperties setTopicSource(String topicSource) {
        this.topicSource = topicSource;
        return this;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public RocketMQProperties setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
        return this;
    }

    public int getTranMessageSendInitialDelay() {
        return tranMessageSendInitialDelay;
    }

    public RocketMQProperties setTranMessageSendInitialDelay(int tranMessageSendInitialDelay) {
        this.tranMessageSendInitialDelay = tranMessageSendInitialDelay;
        return this;
    }

    public long getTranMessageSendPeriod() {
        return tranMessageSendPeriod;
    }

    public RocketMQProperties setTranMessageSendPeriod(long tranMessageSendPeriod) {
        this.tranMessageSendPeriod = tranMessageSendPeriod;
        return this;
    }

    public int getTranMessageSendCorePoolSize() {
        return tranMessageSendCorePoolSize;
    }

    public RocketMQProperties setTranMessageSendCorePoolSize(int tranMessageSendCorePoolSize) {
        this.tranMessageSendCorePoolSize = tranMessageSendCorePoolSize;
        return this;
    }
}
