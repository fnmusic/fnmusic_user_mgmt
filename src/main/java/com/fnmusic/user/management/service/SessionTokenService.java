package com.fnmusic.user.management.service;

import com.fnmusic.user.management.redis.RedisCacheRepository;
import com.fnmusic.user.management.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SessionTokenService {

    @Autowired
    private RedisCacheRepository redisCacheRepository;
    private Object tokenCache;

    @PostConstruct
    public void init() {
        this.tokenCache = redisCacheRepository.createCache(Utils.APPNAME,"tokenCache",7200000);
    }

    public boolean contains(String token) {
        return redisCacheRepository.isPresent(tokenCache,token);
    }


    public Authentication retrieve(String token) {
       return (Authentication) redisCacheRepository.get(tokenCache,token);
    }
}
