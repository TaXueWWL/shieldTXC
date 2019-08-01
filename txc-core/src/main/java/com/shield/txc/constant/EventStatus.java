package com.shield.txc.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:15
 * @className EventStatus
 * @desc 事件状态
 */
public enum EventStatus {

    /**初始化*/
    PRODUCE_INIT,
    /**处理中*/
    PRODUCE_PROCESSING,
    /**完成*/
    PRODUCE_PROCESSED,

    /**发布后初始化*/
    CONSUME_INIT,
    /**发布后处理中*/
    CONSUME_PROCESSING,
    /**发布后处理完成*/
    CONSUME_PROCESSED
}
