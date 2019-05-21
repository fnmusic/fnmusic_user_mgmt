package com.fnmusic.user.management.redis;

import com.fnmusic.base.repository.AbstractRedisCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisCacheRepository extends AbstractRedisCacheRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static Logger logger = LoggerFactory.getLogger(RedisCacheRepository.class);

    @Override
    public void init() {
        this.abstractRedisTemplate = redisTemplate;
    }


}
