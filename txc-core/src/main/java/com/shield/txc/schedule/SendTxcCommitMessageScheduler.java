package com.shield.txc.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 9:24
 * @className SendTxcMessageScheduler
 * @desc 发送事务提交消息调度线程
 */
public class SendTxcCommitMessageScheduler {

    private static final Logger log = LoggerFactory.getLogger(SendTxcCommitMessageScheduler.class);



    public String getServiceName() {
        return SendTxcCommitMessageScheduler.class.getSimpleName();
    }
}
