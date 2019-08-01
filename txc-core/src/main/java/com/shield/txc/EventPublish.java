package com.shield.txc;

import com.shield.txc.constant.MessageQueueType;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:24
 * @className EventPublish
 * @desc 事件发布接口
 */
public interface EventPublish {

    /**消息队列实现类型*/
    MessageQueueType messageQueueType();

    void init();

    void start();

    void shutdown();
}
