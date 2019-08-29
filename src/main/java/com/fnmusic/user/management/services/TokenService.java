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
import java.util.Random;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private CacheRepository cacheRepository;
    @Value("${app.services.token.redisTtl}")
    private long redisTtl;

    private Object tokenCache;
    private static Logger logger = LoggerFactory.getLogger(TokenService.class);

    @PostConstruct
    public void init() {
        this.tokenCache = cacheRepository.createCache(ConstantUtils.APPNAME,"tokenCache",redisTtl);

    }

    public String generateUserAccessToken(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(UUID.randomUUID().toString());
        }

        return sb.toString();
    }

    public String generateToken(int tokenLength) {
        final String CONSTVALUES = "abcdefghijklmnopqrstuvwxyz1234567890";
        char[] charArray = CONSTVALUES.toCharArray();

        String token = "";
        Random rnd = new Random();
        for (int i = 0; i < tokenLength; i++) {
            int index = rnd.nextInt(charArray.length - 1);
            token += charArray[index];
        }

        return token;
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
