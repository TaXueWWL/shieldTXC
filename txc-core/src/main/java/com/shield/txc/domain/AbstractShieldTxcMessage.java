package com.shield.txc.domain;

import java.io.Serializable;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 20:21
 * @className ShieldTxcMessage
 * @desc 事务消息抽象类，业务方需要继承该类
 */
public abstract class AbstractShieldTxcMessage implements Serializable {

    private static final long serialVersionUID = -2416427331208398607L;

    /**消息序列化*/
    public abstract String encode();

    /**消息反序列化*/
    public abstract void decode(String msg);
}
