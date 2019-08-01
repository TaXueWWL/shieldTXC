package com.shield.txc;

import org.junit.Test;

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
                .setId("14");
        System.out.println("编码后:" + rollbackMessage.encode());

        ShieldTxcMessage rollbackMessage1 = new ShieldTxcMessage();
        rollbackMessage1.decode(rollbackMessage.encode());
        System.out.println("解码后:" + rollbackMessage1.encode());
    }

}
