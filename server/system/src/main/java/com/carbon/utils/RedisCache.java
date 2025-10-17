package com.carbon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类，使用 Jackson 进行 JSON 序列化和反序列化
 */
@Component
@Slf4j
public class RedisCache {

    @Resource
    public RedisTemplate<String, Object> redisTemplate;

    // Jackson 的核心类，用于 JSON 操作
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 缓存基本对象，如 Integer, String, 实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            // 处理序列化异常，例如记录日志
            log.error(e.getMessage());
        }
    }

    /**
     * 缓存基本对象，并设置过期时间
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, timeout, timeUnit);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化异常 {}",e.getMessage());
        }
    }

    /**
     * 根据键值获取缓存的对象
     * 注意：为了类型安全，此方法需要传入对象的 Class 类型
     *
     * @param key   缓存的键值
     * @param clazz 期望返回的对象类型
     * @return 缓存的对象
     */
    public <T> T getCacheObject(final String key, final Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        try {
            // 由于 RedisTemplate 默认序列化器可能直接返回字符串，我们将其转为 String
            String jsonValue = (String) obj;
            return objectMapper.readValue(jsonValue, clazz);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            // 出现反序列化异常时，可以考虑删除这个损坏的键
            deleteObject(key);
            return null;
        }
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存的键值
     */
    public void deleteObject(final String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key      缓存的键值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return true 成功, false 失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }
}