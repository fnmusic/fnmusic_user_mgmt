package com.fnmusic.user.management.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TokenService {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private CacheRepository cacheRepository;
    @Value("${app.redisTtl}")
    private long redisTtl;

    private Object tokenCache;
    private static Logger logger = LoggerFactory.getLogger(TokenService.class);

    @PostConstruct
    public void init() {
        this.tokenCache = cacheRepository.createCache(ConstantUtils.APPNAME,"tokenCache",6L);
    }

    public void store(String token, Object data) {
        cacheRepository.put(tokenCache, token, data);
    }

    public boolean contains(String token) {
        return cacheRepository.isPresent(tokenCache,token);
    }

    public Authentication retrieve(String token) {
        return (Authentication) cacheRepository.get(tokenCache,token);
    }

    public boolean remove(String key) {
        return cacheRepository.remove(tokenCache,key);
    }

    public void clearFromRedisCache() {
        String key = "*";
        this.cacheRepository.clear(tokenCache,key);
    }

}
