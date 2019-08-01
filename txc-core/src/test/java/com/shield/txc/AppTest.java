package com.shield.txc;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue( true );
    }

    @Test
    public void testBuildRollbackMessage() {
        ShieldTxcMessage rollbackMessage = new ShieldTxcMessage();
        rollbackMessage.setAppId("default")
                .setContent("{\"name\":\"snowalker2\"}")
                .setEventStatus("PRODUCE_INIT")
                .setTxType("COMMIT")
                .setEventType("INSERT")
                .setBizKey(UUID.randomUUID().toString())
                .setId("14");
        System.out.println("编码后:" + rollbackMessage.encode());

        ShieldTxcMessage rollbackMessage1 = new ShieldTxcMessage();
        rollbackMessage1.decode(rollbackMessage.encode());
        System.out.println("解码后:" + rollbackMessage1.encode());

        StringBuilder sqlBuilder = new StringBuilder("SELECT id, event_type, event_status, tx_type, content,")
                .append("app_id, record_status, gmt_create, gmt_update, biz_key")
                .append(" from shield_event where biz_key=? and tx_type=? and event_type=? ")
                .append(" AND (event_status = 'CONSUME_INIT' OR event_status = 'CONSUME_PROCESSING' OR event_status = 'CONSUME_PROCESSED')");

        System.out.println(sqlBuilder.toString());
    }

}
