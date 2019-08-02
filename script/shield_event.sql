/*
Navicat MySQL Data Transfer

Source Server         : localhost-3306
Source Server Version : 50505
Source Host           : 127.0.0.1:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-08-01 23:12:31
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for shield_event
-- ----------------------------
DROP TABLE IF EXISTS `shield_event`;
CREATE TABLE `shield_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `event_type` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT 'OTHER' COMMENT '事件类型 INSERT UPDATE DELETE',
  `tx_type` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT 'OTHER' COMMENT '事务类型 COMMIT ROLLBACK',
  `event_status` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT 'INIT' COMMENT '事件处理状态, INIT PUBLISHING PUBLISHED PROCESSING PROCESSED',
  `content` text CHARACTER SET utf8 COMMENT '业务参数',
  `app_id` varchar(128) CHARACTER SET utf8 DEFAULT 'DEFAULT_APP' COMMENT '应用名',
  `record_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据状态，0-有效，1-逻辑删除',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `biz_key` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '-1' COMMENT '业务key',
  PRIMARY KEY (`id`),
  UNIQUE KEY `message_exists_idx` (`tx_type`,`biz_key`,`event_status`,`app_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=latin1 COMMENT='事件表';
