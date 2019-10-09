package com.fnmusic.user.management.services;

import com.fnmusic.base.models.Permission;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.dao.impl.PermissionDao;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PermissionService {

    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private PermissionDao permissionDao;
    @Value("${app.redisTtl}")
    private long redisTtl;

    private static Object permissionCache;
    private static Logger logger = LoggerFactory.getLogger(RoleService.class);

    @PostConstruct
    public void init() {
        permissionCache = cacheRepository.createCache(ConstantUtils.APPNAME,"featureCache",redisTtl);
        clearFromRedisCache();
    }

    private void create(Permission permission) {
        try {
            permissionDao.create(permission);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Permission> retrievePermissionById(int id) {
        try {
            String key = "permission_retrievePermissionById_id_"+id+"";
            Result<Permission> data = (Result<Permission>) cacheRepository.get(permissionCache,key);
            if (data == null) {
                data = permissionDao.retrieveById(id);
                if (data != null) {
                    cacheRepository.put(permissionCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Permission> retrievePermissionByName(String name) {
        try {
            String key = "permission_retrievePermissionByName_name_"+name+"";
            Result<Permission> data = (Result<Permission>) cacheRepository.get(permissionCache, key);
            if (data == null) {
                data = permissionDao.retrieveByName(name);
                if (data != null) {
                    cacheRepository.put(permissionCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Permission> retrievePermissionsByFeature(String feature) {

        try {
            String key = "permission_retrievePermissionsByFeature_feature_"+feature+"";
            Result<Permission> data = (Result<Permission>) cacheRepository.get(permissionCache,key);
            if (data == null) {
                int pageSize = 30;
                data = permissionDao.retrieveByFeature(feature,1,30);
                while (data.getList().size() < data.getNoOfRecords()) {
                    Result<Permission> result = permissionDao.retrieveByFeature(feature,data.getPageNumber() + 1,pageSize);
                    data.getList().addAll(result.getList());
                    data.setPageNumber(result.getPageNumber());
                    if (data.getList().size() >= data.getNoOfRecords()) {
                        break;
                    }
                }

                if (data != null) {
                    cacheRepository.put(permissionCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Permission> retrieveAllPermissions(int pageNumber, int pageSize) {
        try {
            String key = "permission_retrieveAllPermissions_pageNumber"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Permission> data = (Result<Permission>) cacheRepository.get(permissionCache, key);
            if (data == null) {
                data = permissionDao.retrieveAll(pageNumber,pageSize);
                if (data != null) {
                    cacheRepository.put(permissionCache,key,data);
                }
            }
            return data;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public void update(Permission permission) {
        try {
            permissionDao.update(permission);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void delete(int id) {
        try {
            permissionDao.delete(id);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void clearFromRedisCache() {
        cacheRepository.clear(permissionCache,"permission_*");
    }
}
