package com.shield.txc.domain;

import java.util.Date;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/28 23:09
 * @className ShieldEvent
 * @desc event映射实体
 */
public class ShieldEvent {

    private Boolean success = Boolean.FALSE;
    /**
     * 自增主键
     */
    private Integer id;
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
     * 记录状态
     */
    private int recordStatus;
    /**
     * 更新前状态
     */
    private String beforeUpdateEventStatus;
    private Date gmtCreate;
    private Date gmtUpdate;

    public String getTxType() {
        return txType;
    }

    public ShieldEvent setTxType(String txType) {
        this.txType = txType;
        return this;
    }

    public String getBeforeUpdateEventStatus() {
        return beforeUpdateEventStatus;
    }

    public ShieldEvent setBeforeUpdateEventStatus(String beforeUpdateEventStatus) {
        this.beforeUpdateEventStatus = beforeUpdateEventStatus;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public ShieldEvent setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public ShieldEvent setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public ShieldEvent setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public ShieldEvent setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ShieldEvent setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public ShieldEvent setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public ShieldEvent setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
        return this;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public ShieldEvent setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
        return this;
    }

    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    public ShieldEvent setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
        return this;
    }

    @Override
    public String toString() {
        return "ShieldEvent{" +
                "success=" + success +
                ", id=" + id +
                ", eventType='" + eventType + '\'' +
                ", eventStatus='" + eventStatus + '\'' +
                ", content='" + content + '\'' +
                ", appId='" + appId + '\'' +
                ", recordStatus=" + recordStatus +
                ", beforeUpdateEventStatus='" + beforeUpdateEventStatus + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtUpdate=" + gmtUpdate +
                '}';
    }
}
