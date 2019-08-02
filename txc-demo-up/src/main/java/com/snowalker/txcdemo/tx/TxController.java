package com.snowalker.txcdemo.tx;

import com.shield.txc.ShieldTxcRocketMQProducerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 17:29
 * @className
 * @desc
 */
@Controller
public class TxController implements CommandLineRunner {

    @Autowired
    ShieldTxcRocketMQProducerClient rocketMQEventProducerClient;

    @Autowired
    TxService txService;

    @Override
    public void run(String... args) throws Exception {
//        System.out.println(rocketMQEventProducerClient.getNameSrvAddr());
        System.out.println("事务上游本地事务开始，消息持久化.....");
        txService.testTran();
    }
}
