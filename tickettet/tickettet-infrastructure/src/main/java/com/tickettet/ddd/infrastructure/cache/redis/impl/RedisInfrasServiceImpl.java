package com.tickettet.ddd.infrastructure.cache.redis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickettet.ddd.infrastructure.cache.redis.RedisInfrasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisInfrasServiceImpl implements RedisInfrasService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void setString(String key, String value) {
        if (!StringUtils.hasLength(key)) {
            // null or ''
            log.warn("Cache :: key is empty!");
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String getString(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(String::valueOf)
                .orElse(null);
    }

    @Override
    public void setObject(String key, Object value) {
        if (!StringUtils.hasLength(key)) {
            log.warn("Cache :: key is empty!");
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("SetObject error: {}", e.getMessage());
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> targetClass) {
        Object result = redisTemplate.opsForValue().get(key);
        log.info("get Cache: {}", result);
        if (result == null) {
            return null;
        }
        // if result is a linkedHashMap
        if (result instanceof Map) {
            try {
                return objectMapper.convertValue(result, targetClass);
            } catch (Exception e) {
                log.error("Error converting LinkedHashMap to object: {}", e.getMessage());
                return null;
            }
        }
        // if result as a String
        if (result instanceof String) {
            try {
                return objectMapper.readValue((String) result, targetClass);
            } catch (Exception e) {
                log.error("Error deserializing JSON to object: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public void setObjectWithTTL(String key, Object value, long timeout, TimeUnit timeUnit) {
        if(!StringUtils.hasLength(key)){
            log.warn("Cache :: key is empty!");
            return;
        }
        try{
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }catch(Exception e){
            log.error("setObjectWithTTL error: {}", e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
