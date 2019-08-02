package com.snowalker.txcdemoup.tx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shield.txc.domain.AbstractShieldTxcMessage;

import java.io.IOException;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/7/31 20:30
 * @className
 * @desc
 */
public class TestTxMessage extends AbstractShieldTxcMessage {

    private String name;

    @Override
    public String encode() {
        ObjectMapper objectMapper = new ObjectMapper();
        String ret_string = null;
        try {
            ret_string = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("TestTxMessage消息序列化json异常", e);
        }
        return ret_string;
    }

    @Override
    public void decode(String msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(msg);
            this.setName(root.get("name").asText());
        } catch (IOException e) {
            throw new RuntimeException("TestTxMessage反序列化消息异常", e);
        }
    }

    public String getName() {
        return name;
    }

    public TestTxMessage setName(String name) {
        this.name = name;
        return this;
    }
}
