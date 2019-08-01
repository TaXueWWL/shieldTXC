package com.shield.txc.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:51
 * @className CommonProperty
 * @desc 公共常量
 */
public abstract class CommonProperty {

    private CommonProperty() {}

    public static final String TRANSACTION_COMMMIT_STAGE = "SHIELD_TXC_COMMIT_";

    public static final String TRANSACTION_ROLLBACK_STAGE = "SHIELD_TX_ROLLBACK_";

    public static final String DEFAULT_SHIELD_TXC_TOPIC = "DEFAULT_TXC_XXX";


    public static final String LOGGER_NAME = "shieldTxcCommon";
}
