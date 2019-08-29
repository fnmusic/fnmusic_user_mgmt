package com.fnmusic.user.management.repository;

import com.fnmusic.base.repository.impl.AbstractCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CacheRepository extends AbstractCacheRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static Logger logger = LoggerFactory.getLogger(CacheRepository.class);

    @Override
    public void init() {
        this.abstractRedisTemplate = redisTemplate;
    }


}
