package com.shield.txc.schedule;

import com.shield.txc.domain.ShieldEvent;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 13:56
 * @className AbstractMessageScheduler
 * @desc 抽象消息发送调度
 */
public abstract class AbstractMessageScheduler {

    /**
     * 发送前置处理
     * @param shieldEvent
     */
    public abstract void processBeforeSendMessage(ShieldEvent shieldEvent);

    /**
     * 发送过程
     * @param shieldEvent
     */
    public abstract void sendMessage(ShieldEvent shieldEvent);

    /**
     * 发送后置处理
     * @param shieldEvent
     */
    public abstract void processAfterSendMessage(ShieldEvent shieldEvent);

}
