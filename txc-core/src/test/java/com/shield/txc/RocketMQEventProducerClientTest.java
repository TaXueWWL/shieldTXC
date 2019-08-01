package com.shield.txc;

import com.shield.txc.constant.CommonProperty;
import org.apache.rocketmq.common.message.Message;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/29 9:55
 * @className
 * @desc
 */
public class RocketMQEventProducerClientTest {

    public static void main(String[] args) throws Exception {
        testCase00();
        System.out.println(new StringBuilder("GID_")
                .append(CommonProperty.TRANSACTION_COMMMIT_STAGE)
                .append("SNOWALKER_TOPIC")
                .toString());
    }


    /**
     * 测试构造方式初始化
     * @throws Exception
     */
    static void testCase00() throws Exception {
        String topic = "SNOWALKER_TOPIC2";
        String nameSrvAddr = "172.30.66.50:9876;172.30.66.51:9876";
        int retryTimesWhenSendFailed = 3;
        ShieldTxcRocketMQProducerClient rocketmq =
                new ShieldTxcRocketMQProducerClient(topic + "123", nameSrvAddr, retryTimesWhenSendFailed);
        Message message = new Message(topic, "TO Be Continued....".getBytes());
        rocketmq.getCommitProducer().send(message);
        rocketmq.shutdown();
    }

}
