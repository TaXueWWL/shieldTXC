package com.shield.txc.schedule;

import com.shield.txc.BaseEventService;
import com.shield.txc.ShieldTxcRocketMQProducerClient;
import com.shield.txc.constant.EventStatus;
import com.shield.txc.domain.ShieldEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 9:25
 * @className SendTxcRollbackMessageScheduler
 * @desc 发送回滚消息调度线程
 */
public class SendTxcRollbackMessageScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SendTxcRollbackMessageScheduler.class);

    ScheduledExecutorService executorService;

    private Runnable command;

    private long initialDelay = 0;

    private long period = 5;

    private int corePoolSize = 1;

    BaseEventService baseEventService;

    ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public SendTxcRollbackMessageScheduler() {
        executorService = Executors.newScheduledThreadPool(corePoolSize);
    }

    public SendTxcRollbackMessageScheduler(int initialDelay,
                                           int period,
                                           int corePoolSize) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.corePoolSize = corePoolSize;
        executorService = Executors.newScheduledThreadPool(this.corePoolSize);
    }

    @Override
    public void run() {
        // TODO 查询并发消息
        System.out.println("模拟发送开始!!!!" + new Date(System.currentTimeMillis()));
        // 获取待调度的消息
        List<ShieldEvent> shieldEvents = baseEventService.queryEventListByStatus(EventStatus.PRODUCE_INIT.toString());
        System.out.println(shieldEvents.toString());
        // 迭代

        // 发送
    }

    public void schedule() {
        executorService.scheduleAtFixedRate(
                this,
                this.initialDelay,
                this.period,
                this.timeUnit);
    }

    public String getServiceName() {
        return SendTxcRollbackMessageScheduler.class.getSimpleName();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public SendTxcRollbackMessageScheduler setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public SendTxcRollbackMessageScheduler setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public long getPeriod() {
        return period;
    }

    public SendTxcRollbackMessageScheduler setPeriod(long period) {
        this.period = period;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public BaseEventService getBaseEventService() {
        return baseEventService;
    }

    public SendTxcRollbackMessageScheduler setBaseEventService(BaseEventService baseEventService) {
        this.baseEventService = baseEventService;
        return this;
    }

    public ShieldTxcRocketMQProducerClient getShieldTxcRocketMQProducerClient() {
        return shieldTxcRocketMQProducerClient;
    }

    public SendTxcRollbackMessageScheduler setShieldTxcRocketMQProducerClient(ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient) {
        this.shieldTxcRocketMQProducerClient = shieldTxcRocketMQProducerClient;
        return this;
    }
}
