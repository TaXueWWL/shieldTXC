package com.shield.txc;

import com.shield.txc.domain.ShieldEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/30 22:58
 * @className BaseEventRepository
 * @desc 基础事件持久层
 * TODO 表名可配置
 */
public class BaseEventService {

    BaseEventRepository baseEventRepository;

    public BaseEventService(BaseEventRepository baseEventRepository) {
        this.baseEventRepository = baseEventRepository;
    }

    /**
     * 插入事件
     *
     * @param event
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEvent(ShieldEvent event) {
        return baseEventRepository.insertEvent(event);
    }

    /**
     * 带id插入事件
     * @param event
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventWithId(ShieldEvent event) {
        return baseEventRepository.insertEventWithId(event);
    }

    /**
     * 更新事件状态
     *
     * @param event
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEventStatusById(ShieldEvent event) {
        return baseEventRepository.updateEventStatusById(event);
    }

    /**
     * 逻辑删除事件
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEventLogicallyById(Integer id) {
        return baseEventRepository.deleteEventLogicallyById(id);
    }

    /**
     * 根据事件状态获取事件列表
     *
     * @param eventStatus
     * @return
     */
    public List<ShieldEvent> queryEventListByStatus(String eventStatus) {
        return baseEventRepository.queryEventListByStatus(eventStatus);
    }

    /**
     * 查询事件详情
     *
     * @param id
     * @return
     */
    public ShieldEvent queryEventById(int id) {
        return baseEventRepository.queryEventById(id);
    }

    /**
     * 查询事件详情
     * @param bizKey
     * @param txType
     * @param appId
     * @return
     */
    public List<ShieldEvent> queryEventByBizkeyCond(String bizKey, String txType, String appId) {
        return baseEventRepository.queryEventByBizkeyCond(bizKey, txType, appId);
    }
}
