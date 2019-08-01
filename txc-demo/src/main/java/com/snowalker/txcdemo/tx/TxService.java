package com.snowalker.txcdemo.tx;

import com.shield.txc.ShieldTxcRocketMQProducerClient;
import com.shield.txc.constant.EventType;
import com.shield.txc.constant.TXType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 22:38
 * @className
 * @desc
 */
@Service
public class TxService {

    @Autowired
    ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient;

    @Transactional(rollbackFor = Exception.class)
    public void testTran() {
        TestTxMessage testTxMessage = new TestTxMessage();
        testTxMessage.setName("SNOWALKER");
        shieldTxcRocketMQProducerClient
                .putMessage(testTxMessage, EventType.INSERT, TXType.COMMIT, "txc-demo");
        throw new RuntimeException("测试事务回滚");
    }
}
