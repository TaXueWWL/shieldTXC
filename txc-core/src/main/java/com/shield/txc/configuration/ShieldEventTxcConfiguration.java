package com.shield.txc.configuration;

import com.google.common.base.Preconditions;
import com.shield.txc.BaseEventRepository;
import com.shield.txc.BaseEventService;
import com.shield.txc.ShieldTxcRocketMQProducerClient;
import com.shield.txc.exception.BizException;
import com.shield.txc.schedule.SendTxcMessageScheduler;
import com.shield.txc.schedule.SendTxcProcessingMessageScheduler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 15:29
 * @className JDBCTemplateConfiguration
 * @desc ShieldEventTxcConfiguration
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class ShieldEventTxcConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShieldEventTxcConfiguration.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 持久化类构造
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(value = 0)
    public BaseEventRepository baseEventRepository() {
        BaseEventRepository baseEventRepository = new BaseEventRepository(jdbcTemplate);
        LOGGER.debug("Initializing [BaseEventRepository] instance init success.");
        return baseEventRepository;
    }

    /**
     * 持久化service构造
     * @param baseEventRepository
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(value = 1)
    public BaseEventService baseEventService(BaseEventRepository baseEventRepository) {
        BaseEventService baseEventService = new BaseEventService(baseEventRepository);
        LOGGER.debug("Initializing [BaseEventService] instance init success.");
        return baseEventService;
    }

    /**
     * RocketMQ事件生产端构造
     * @param rocketMQProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(value = 2)
    public ShieldTxcRocketMQProducerClient rocketMQEventProducerClient(RocketMQProperties rocketMQProperties) {
        String nameSrvAddr = rocketMQProperties.getNameSrvAddr();
        Preconditions.checkNotNull(nameSrvAddr, "please set 'shield.event.rocketmq.nameSrvAddr' which can not be NULL!");
        String topicSource = rocketMQProperties.getTopicSource();
        if (StringUtils.isBlank(topicSource)) {
            throw new BizException("make sure config value of 'shield.event.rocketmq.topicSource' not empty! ");
        }
        int retryTimesWhenSendFailed = rocketMQProperties.getRetryTimesWhenSendFailed();

        ShieldTxcRocketMQProducerClient rocketMQEventProducerClient =
                new ShieldTxcRocketMQProducerClient(topicSource, nameSrvAddr, retryTimesWhenSendFailed);
        rocketMQEventProducerClient.setBaseEventService(baseEventService(baseEventRepository()));
        LOGGER.debug("Initializing [ShieldTxcRocketMQProducerClient] instance init success.");
        return rocketMQEventProducerClient;
    }

    /**
     * 异步回滚消息调度构造
     * @param rocketMQProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(value = 3)
    public SendTxcMessageScheduler sendTxcRollbackMessageScheduler(RocketMQProperties rocketMQProperties) {
        SendTxcMessageScheduler sendTxcRollbackMessageScheduler = new SendTxcMessageScheduler();
        // 设置调度线程池参数
        sendTxcRollbackMessageScheduler.setInitialDelay(rocketMQProperties.getTranMessageSendInitialDelay());
        sendTxcRollbackMessageScheduler.setPeriod(rocketMQProperties.getTranMessageSendPeriod());
        sendTxcRollbackMessageScheduler.setCorePoolSize(rocketMQProperties.getTranMessageSendCorePoolSize());
        // 数据库操作
        sendTxcRollbackMessageScheduler.setBaseEventService(baseEventService(baseEventRepository()));
        // 消息发送
        sendTxcRollbackMessageScheduler.setShieldTxcRocketMQProducerClient(rocketMQEventProducerClient(rocketMQProperties));
        LOGGER.debug("Initializing [SendTxcRollbackMessageScheduler] instance init success.");
        // 执行调度
        sendTxcRollbackMessageScheduler.schedule();
        return sendTxcRollbackMessageScheduler;
    }

    /**
     * 异步回滚消息调度构造
     * @param rocketMQProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(value = 4)
    public SendTxcProcessingMessageScheduler sendTxcProcessingMessageScheduler(RocketMQProperties rocketMQProperties) {
        SendTxcProcessingMessageScheduler sendTxcProcessingMessageScheduler = new SendTxcProcessingMessageScheduler();
        // 设置调度线程池参数
        sendTxcProcessingMessageScheduler.setInitialDelay(rocketMQProperties.getTranMessageSendInitialDelay());
        sendTxcProcessingMessageScheduler.setPeriod(rocketMQProperties.getTranMessageSendPeriod());
        sendTxcProcessingMessageScheduler.setCorePoolSize(rocketMQProperties.getTranMessageSendCorePoolSize());
        // 数据库操作
        sendTxcProcessingMessageScheduler.setBaseEventService(baseEventService(baseEventRepository()));
        // 消息发送
        sendTxcProcessingMessageScheduler.setShieldTxcRocketMQProducerClient(rocketMQEventProducerClient(rocketMQProperties));
        LOGGER.debug("Initializing [SendTxcProcessingMessageScheduler] instance init success.");
        // 执行调度
        sendTxcProcessingMessageScheduler.schedule();
        return sendTxcProcessingMessageScheduler;
    }

}
