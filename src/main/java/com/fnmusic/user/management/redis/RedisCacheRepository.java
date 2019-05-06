package com.fnmusic.user.management.redis;

import com.fnmusic.user.management.model.RedisCacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisCacheRepository implements ICacheRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(RedisCacheRepository.class);

    @Override
    public Object createCache(String appName, String cacheName, long ttl) {
        RedisCacheConfig config = new RedisCacheConfig();
        config.setCacheName(appName + ":" + cacheName + ":");
        config.setTtl(ttl);
        config.setTtlEnabled(ttl > 0);

        return config;
    }

    @Override
    public void destroyCache(Object cache) {

    }

    @Override
    public void put(Object cache, String key, Object data) {
        RedisCacheConfig config = (RedisCacheConfig) cache;
        if (config != null) {
            String cacheKey = config.getCacheName() + key;
            if (config != null) {
                this.redisTemplate.opsForValue().set(cacheKey,data);
                if (config.isTtlEnabled()) {
                    this.redisTemplate.expire(cacheKey, config.getTtl(), TimeUnit.SECONDS);
                }
            }
        }
    }

    @Override
    public Object get(Object cache, String key) {

        try {
            RedisCacheConfig config = new RedisCacheConfig();
            if (config != null) {
                String cacheKey = config.getCacheName() + key;
                if (config.isTtlEnabled()) {
                    this.redisTemplate.expire(cacheKey, config.getTtl(), TimeUnit.SECONDS);
                }
                return this.redisTemplate.opsForValue().get(cacheKey);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public boolean isPresent(Object cache, String key) {
        return false;
    }

    @Override
    public boolean remove(Object cache, String key) {
        return false;
    }
}
