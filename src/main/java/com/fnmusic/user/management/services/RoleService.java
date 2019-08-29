package com.fnmusic.user.management.services;

import com.fnmusic.base.models.Feature;
import com.fnmusic.base.models.Permission;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.dao.impl.RoleDao;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RoleService {

    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private RoleDao roleDao;
    @Value("${app.redisTtl}")
    private long redisTtl;

    private static Object roleCache;
    private static Logger logger = LoggerFactory.getLogger(RoleService.class);

    @PostConstruct
    public void init() {
        roleCache = cacheRepository.createCache(ConstantUtils.APPNAME,"roleCache",redisTtl);
        clearFromRedisCache();
    }

    public void clearFromRedisCache() {
        String key = "role_*";
        cacheRepository.clear(roleCache,key);
    }
}
