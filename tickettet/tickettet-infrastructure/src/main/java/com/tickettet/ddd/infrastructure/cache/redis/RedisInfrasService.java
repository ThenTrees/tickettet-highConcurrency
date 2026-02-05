package com.tickettet.ddd.infrastructure.cache.redis;

import java.util.concurrent.TimeUnit;

public interface RedisInfrasService {

    void setString(String key, String value);
    String getString(String key);

    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass);

    // set object with ttl
    void setObjectWithTTL(String key, Object value, long timeout, TimeUnit timeUnit);
    // deleted by key
    void delete(String key);
}
