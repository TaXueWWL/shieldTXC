package com.shield.txc;

import com.shield.txc.constant.MessageQueueType;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:25
 * @className EventSubscribe
 * @desc 事件订阅
 */
public interface EventSubscribe {

    /**消息队列实现类型*/
    MessageQueueType messageQueueType();

    void init();

    void start();

    void shutdown();
}
