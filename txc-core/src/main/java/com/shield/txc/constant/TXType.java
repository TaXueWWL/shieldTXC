package com.shield.txc.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:14
 * @className EventType
 * @desc 事务类型，是回滚还是提交
 */
public enum TXType {

    COMMIT,
    ROLLBACK;

    public static String getCommit() {
        return COMMIT.toString();
    }

    public static String getRollback() {
        return ROLLBACK.toString();
    }
}
