package com.fnmusic.user.management.services;

import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.dao.SessionDao;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SessionService {

    @Autowired
    private SessionDao sessionDao;
    @Autowired
    private CacheRepository cacheRepository;
    @Value("${app.redisTtl}")
    private long redisTtl;

    private Object sessionCache;
    private static Logger logger = LoggerFactory.getLogger(SessionService.class);

    @PostConstruct
    public void init() {
        sessionCache = cacheRepository.createCache(ConstantUtils.APPNAME,"sessionCache",redisTtl);
    }

    public void addSession() {

    }

    public void clearFromRedisCache() {
        cacheRepository.clear(sessionCache,"session_*");
    }

}
