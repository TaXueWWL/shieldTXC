package com.snowalker.txcdemo.tx;

import com.shield.txc.ShieldTxcRocketMQProducerClient;
import com.shield.txc.constant.EventType;
import com.shield.txc.constant.TXType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        testTxMessage.setName(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        shieldTxcRocketMQProducerClient
                .putMessage(testTxMessage,
                        EventType.INSERT,
                        TXType.COMMIT,
                        testTxMessage.getName(),
                        UUID.randomUUID().toString());
    }
}
