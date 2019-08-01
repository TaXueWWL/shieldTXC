package com.shield.txc.constant;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 19:49
 * @className BizCode
 * @desc 业务返回码
 */
public abstract class BizCode {

    private BizCode() {}

    /**发送消息成功*/
    public static final int SEND_MESSAGE_SUCC = 0;

    /**发送消息失败*/
    public static final int SEND_MESSAGE_FAIL = 1;
}
