package com.shield.txc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.shield.txc.domain.AbstractShieldTxcMessage;

import java.io.IOException;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 11:23
 * @className RollbacklMessage
 * @desc 事务回滚消息
 */
public class ShieldTxcMessage extends AbstractShieldTxcMessage {

    /**
     * 自增主键
     */
    private String id;
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 事务类型
     */
    private String txType;
    /**
     * 事件状态
     */
    private String eventStatus;
    /**
     * 业务实体
     */
    private String content;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 业务键
     */
    private String bizKey;

    @Override
    public String encode() {

        ImmutableMap<String, Object> messageBody = new ImmutableMap.Builder<String, Object>()
                .put("id", String.valueOf(this.getId()))
                .put("eventType", this.getEventType())
                .put("txType", this.getTxType())
                .put("eventStatus", this.getEventStatus())
                .put("content", this.getContent())
                .put("appId", this.getAppId())
                .put("bizKey", this.getBizKey())
                .build();

        String ret_string = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ret_string = objectMapper.writeValueAsString(messageBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("RollbackMessage消息序列化json异常", e);
        }
        return ret_string;
    }

    @Override
    public void decode(String msg) {
        Preconditions.checkNotNull(msg);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(msg);
            this.setId(root.get("id").asText());
            this.setEventType(root.get("eventType").asText());
            this.setTxType(root.get("txType").asText());
            this.setEventStatus(root.get("eventStatus").asText());
            this.setContent(root.get("content").asText());
            this.setAppId(root.get("appId").asText());
            this.setBizKey(root.get("bizKey").asText());
        } catch (IOException e) {
            throw new RuntimeException("NineYiPriceQueryTaskProtocol反序列化消息异常", e);
        }
    }

    public String getId() {
        return id;
    }

    public ShieldTxcMessage setId(String id) {
        this.id = id;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public ShieldTxcMessage setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getTxType() {
        return txType;
    }

    public ShieldTxcMessage setTxType(String txType) {
        this.txType = txType;
        return this;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public ShieldTxcMessage setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ShieldTxcMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public ShieldTxcMessage setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getBizKey() {
        return bizKey;
    }

    public ShieldTxcMessage setBizKey(String bizKey) {
        this.bizKey = bizKey;
        return this;
    }
}
