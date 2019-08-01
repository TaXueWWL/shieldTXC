package com.snowalker.txcdemo;

import com.shield.txc.BaseEventRepository;
import com.shield.txc.ShieldTxcRocketMQProducerClient;
import com.shield.txc.constant.EventStatus;
import com.shield.txc.constant.EventType;
import com.shield.txc.constant.TXType;
import com.shield.txc.domain.ShieldEvent;
import com.snowalker.txcdemo.tx.TestTxMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseEventRepositoryTestCase {

    @Autowired
    BaseEventRepository repository;

    @Autowired
    ShieldTxcRocketMQProducerClient shieldTxcRocketMQProducerClient;

    @Test
    public void contextLoads() {
    }

    /**
     * 测试事件插入
     */
    @Test
    public void testInsertEvent() {
        ShieldEvent event = new ShieldEvent();
        event.setEventType(EventType.INSERT.toString())
                .setTxType(TXType.COMMIT.toString())
                .setAppId("txc-demo")
                .setContent("{\"name\":\"snowalker2\"}")
                .setEventStatus(EventStatus.PRODUCE_INIT.toString());
        repository.insertEvent(event);
    }

    /**
     * 测试通过id更新
     */
    @Test
    public void testUpdateEventById() {
        ShieldEvent event = new ShieldEvent();
        event.setId(1)
                .setEventStatus(EventStatus.PRODUCE_PROCESSED.toString())
                .setBeforeUpdateEventStatus(EventStatus.PRODUCE_PROCESSING.toString());
        repository.updateEventStatusById(event);
    }


    /**
     * 测试逻辑删除byId
     */
    @Test
    public void testDeleteById() {
        int id = 2;
        repository.deleteEventLogicallyById(id);
    }

    /**
     * 测试根据状态查询列表
     */
    @Test
    public void testQueryListByStatus() {
        String eventStatus = EventStatus.PRODUCE_PROCESSING.toString();
        System.out.println(repository.queryEventListByStatus(eventStatus));
    }

    /**
     * 测试通过id查询
     */
    @Test
    public void testQueryById() {
        int id = 1;
        System.out.println(repository.queryEventById(id));
    }

    /**
     * 测试插入事件
     * 事务
     */
    @Test
    public void testInsertByTransaction() {
        TestTxMessage testTxMessage = new TestTxMessage();
        testTxMessage.setName("SNOWALKER");
        shieldTxcRocketMQProducerClient
                .putMessage(testTxMessage, EventType.INSERT, TXType.COMMIT, "txc-demo");
    }

}
