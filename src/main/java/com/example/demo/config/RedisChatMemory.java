package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class RedisChatMemory implements ChatMemory, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(RedisChatMemory.class);

    private static final String DEFAULT_KEY_PREFIX = "chat:";

    private final JedisPool jedisPool;


    private final ObjectMapper objectMapper;

    public RedisChatMemory(
            @Value("${spring.data.redis.host:127.0.0.1}") String host,
            @Value("${spring.data.redis.port:6379}") int port,
            @Value("${spring.data.redis.password:}") String password) {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        String redisPassword = password.isEmpty() ? null : password;
        this.jedisPool = new JedisPool(poolConfig, host, port, 20000, redisPassword);
        this.objectMapper = new ObjectMapper();
        logger.info("Redis chat memory configured at {}:{}", host, port);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {

        String key = DEFAULT_KEY_PREFIX + conversationId;

        AtomicLong timestamp = new AtomicLong(System.currentTimeMillis());

        try (Jedis jedis = jedisPool.getResource()) {
            // 使用pipeline批量操作提升性能
            var pipeline = jedis.pipelined();
            messages.forEach(message -> {
                // 存储消息类型和内容的组合：type|content
                String messageType = getMessageType(message);
                String storedValue = messageType + "|" + message.getText();
                pipeline.hset(key, String.valueOf(timestamp.getAndIncrement()), storedValue);
            });
            pipeline.sync();
        }

        logger.info("Added messages to conversationId: {}", conversationId);
    }

    /**
     * 获取消息类型标识
     */
    private String getMessageType(Message message) {
        if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
            return "user";
        } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
            return "assistant";
        } else if (message instanceof org.springframework.ai.chat.messages.SystemMessage) {
            return "system";
        } else {
            return "unknown";
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {

        String key = DEFAULT_KEY_PREFIX + conversationId;

        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> allMessages = jedis.hgetAll(key);
            if (allMessages.isEmpty()) {
                return List.of();
            }

            return allMessages.entrySet().stream()
                    .sorted((e1, e2) ->
                            Long.compare(Long.parseLong(e1.getKey()), Long.parseLong(e2.getKey()))
                    )
                    .limit(lastN)
                    .map(entry -> createMessageFromStoredValue(entry.getValue()))
                    .collect(Collectors.toList());
        }


    }

    /**
     * 根据存储的值创建对应类型的消息对象
     * 存储格式：type|content
     */
    private Message createMessageFromStoredValue(String storedValue) {
        if (storedValue == null || storedValue.isEmpty()) {
            return new UserMessage("");
        }

        // 检查是否是新格式（以 user|、assistant|、system| 开头）
        if (storedValue.startsWith("user|")) {
            return new UserMessage(storedValue.substring(5));
        } else if (storedValue.startsWith("assistant|")) {
            return new org.springframework.ai.chat.messages.AssistantMessage(storedValue.substring(10));
        } else if (storedValue.startsWith("system|")) {
            return new org.springframework.ai.chat.messages.SystemMessage(storedValue.substring(7));
        } else {
            // 兼容旧数据，默认为用户消息
            logger.debug("兼容旧数据格式，默认为用户消息: {}", storedValue);
            return new UserMessage(storedValue);
        }
    }

    @Override
    public void clear(String conversationId) {

        String key = DEFAULT_KEY_PREFIX + conversationId;

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
        logger.info("Cleared messages for conversationId: {}", conversationId);
    }

    @Override
    public void close() {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis != null) {

                jedis.close();

                logger.info("Redis connection closed.");
            }
            if (jedisPool != null) {

                jedisPool.close();

                logger.info("Jedis pool closed.");
            }
        }

    }

    public void clearOverLimit(String conversationId, int maxLimit, int deleteSize) {
        try {
            String key = DEFAULT_KEY_PREFIX + conversationId;
            try (Jedis jedis = jedisPool.getResource()) {
                List<String> all = jedis.lrange(key, 0, -1);

                if (all.size() >= maxLimit) {
                    all = all.stream().skip(Math.max(0, deleteSize)).toList();
                }
                this.clear(conversationId);
                for (String message : all) {
                    jedis.rpush(key, message);
                }
            }
        }
        catch (Exception e) {
            logger.error("Error clearing messages from Redis chat memory", e);
            throw new RuntimeException(e);
        }
    }

}
