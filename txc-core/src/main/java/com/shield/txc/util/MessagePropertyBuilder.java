package com.shield.txc.util;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 16:42
 * @className MessagePropertyBuilder
 * @desc 消息属性组装
 */
public class MessagePropertyBuilder {

    /**
     * 获取groupId
     * @param tranStage
     * @param topic
     * @return
     */
    public static String groupId(String tranStage, String topic) {
        return new StringBuilder("GID_")
                .append(tranStage)
                .append(topic.toUpperCase())
                .toString();
    }

    /**
     * 获取topic
     * @param tranStage
     * @param topic
     * @return
     */
    public static String topic(String tranStage, String topic) {
        return new StringBuilder(tranStage)
                .append(topic.toUpperCase())
                .toString();
    }
}
