package com.fnmusic.user.management.services;

import com.fnmusic.base.models.Feature;
import com.fnmusic.base.models.Permission;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.dao.impl.FeatureDao;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FeatureService {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private FeatureDao featureDao;
    @Value("${app.redisTtl}")
    private long redisTtl;

    private static Object featureCache;
    private static Logger logger = LoggerFactory.getLogger(RoleService.class);

    @PostConstruct
    public void init() {
        featureCache = cacheRepository.createCache(ConstantUtils.APPNAME,"featureCache",redisTtl);
        clearFromRedisCache();
    }

    public void create(Feature feature) {
        try {
            featureDao.create(feature);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Feature> retrieveById(int id) {
        try {
            String key = "feature_retrieveById_id_"+id+"";
            Result<Feature> data = (Result<Feature>) cacheRepository.get(featureCache,key);
            if (data == null) {
                data = featureDao.retrieveById(id);
                if (data.getData() != null) {
                    Result<Permission> permissionResult = permissionService.retrievePermissionsByFeature(data.getData().getName());
                    data.getData().setPermissions(permissionResult.getList());
                    cacheRepository.put(featureCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Feature> retrieveByName(String name) {
        try {
            String key = "feature_retrieveByName_name_"+name+"";
            Result<Feature> data = (Result<Feature>) cacheRepository.get(featureCache,key);
            if (data == null) {
                data = featureDao.retrieveByName(name);
                if (data.getData() != null) {
                    Result<Permission> permissionResult = permissionService.retrievePermissionsByFeature(data.getData().getName());
                    data.getData().setPermissions(permissionResult.getList());
                    cacheRepository.put(featureCache,key,data);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Feature> retrieveByRole(Role role) {
        try {
            String key = "feature_retrieveByRole_role_"+role+"";
            Result<Feature> data = (Result<Feature>) cacheRepository.get(featureCache,key);
            if (data == null) {
                data = featureDao.retrieveByRole(role.toString());
                if (data.getData() != null) {
                    Result<Permission> permissionResult = permissionService.retrievePermissionsByFeature(data.getData().getName());
                    data.getData().setPermissions(permissionResult.getList());
                    cacheRepository.put(featureCache,key,data);
                }
            }
            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Feature> retrieveAllFeatures(int pageNumber, int pageSize) {
        try {
            String key = "feature_retrieveAllFeatures_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Feature> data = (Result<Feature>) cacheRepository.get(featureCache,key);
            if (data == null) {
                data = featureDao.retrieveAll(pageNumber,pageSize);
                for (Feature feature : data.getList()) {
                    Result<Permission> result = permissionService.retrievePermissionsByFeature(feature.getName());
                    if (!result.getList().isEmpty() || result.getList() != null) {
                        feature.setPermissions(result.getList());
                    }
                }
            }
            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    private void update(Feature feature) {
        try {
            featureDao.update(feature);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void delete(int id) {
        try {
            featureDao.delete(id);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void clearFromRedisCache() {
        cacheRepository.clear(featureCache,"feature_*");
    }
}
