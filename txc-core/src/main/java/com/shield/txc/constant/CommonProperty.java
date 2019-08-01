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

    /**事务提交阶段*/
    public static final String TRANSACTION_COMMMIT_STAGE = "SHIELD_TXC_COMMIT_";

    /**事务回滚阶段*/
    public static final String TRANSACTION_ROLLBACK_STAGE = "SHIELD_TXC_ROLLBACK_";

    /**默认topic源*/
    public static final String DEFAULT_SHIELD_TXC_TOPIC = "DEFAULT_TXC_XXX";

    public static final String LOGGER_NAME = "shieldTxcCommon";

    /**事务下游执行提交最大次数*/
    public static final int MAX_COMMIT_RECONSUME_TIMES = 3;

    /**消息存在*/
    public static final String MESSAGE_HAS_EXISTED_INDEX = "message_exists_idx";

    /**消息存在*/
    public static final String MESSAGE_PRIMARY_KEY_DUPLICATE = "PRIMARY";

}
