package com.fnmusic.user.management.services;

import com.fnmusic.base.models.*;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.base.security.AuthenticationWithToken;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.dao.impl.UserDao;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthService authService;
    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private HashService hashService;
    @Value("${app.services.user.redisTtl}")
    private long redisTtl;

    private static Object userCache;
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostConstruct
    public void init() {
        this.userCache = cacheRepository.createCache(ConstantUtils.APPNAME,"userCache",1L);
        createSuperAdmin();
        clearFromRedisCache();
    }

    private void createSuperAdmin() {

        try {
            User superAdmin = new User();
            superAdmin.setEmail("superadmin@fnmusic.com");
            superAdmin.setUsername("superadmin");
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setPasswordHash(hashService.encode("password123"));
            superAdmin.setRole(Role.SUPER_ADMIN);
            superAdmin.setDateCreated(new Date());

            create(superAdmin);

        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
    }

    public Result<User> create(User user) {

        try {
            return userDao.create(user);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    private void setAuthentication(String accessToken, UserPrincipal userPrincipal) {
        AuthenticationWithToken authentication = new AuthenticationWithToken(userPrincipal,userPrincipal.hashCode(),userPrincipal.getAuthorities());
        authentication.setToken(accessToken);
        tokenService.store(accessToken,authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    public AccessTokenWithUserDetails login(User user) {

        try {
            Result<Feature> featureResult = featureService.retrieveByRole(user.getRole());
            Feature feature = featureResult.getData();

            String accessToken = tokenService.generateUserAccessToken();
            UserPrincipal userPrincipal = new UserPrincipal(user,feature.getPermissions());
            setAuthentication(accessToken,userPrincipal);

            AccessTokenWithUserDetails access = new AccessTokenWithUserDetails();
            access.setAccessToken(accessToken);
            access.setUsername(user.getEmail());
            access.setUser(user);
            access.setFeature(feature);

            return access;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public Result<User> increaseLoginAttempt(String email) {

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE) + 15);
            return userDao.increaseLoginAttempt(email,calendar.getTime());
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> retrieveUserById(Long id) {

        try {
            String key = "user_findUserById_id_"+id+"";
            Result<User> data = (Result<User>) this.cacheRepository.get(userCache,key);
            if (data == null){
                data = userDao.retrieveById(id);
                if (data.getData() != null) {
                    this.cacheRepository.put(userCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> retrieveUserByEmail(String email) {

        try {
            String key = "user_findUserByEmail_email_"+email+"";
            Result<User> data = (Result<User>) this.cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.retrieveByEmail(email);
                if (data.getData() != null) {
                    this.cacheRepository.put(userCache,key,data);
                }
            }

            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> retrieveUserByUsername(String username) {

        try {
            String key = "user_findUserByUsername_username_"+username+"";
            Result<User> data = (Result<User>) this.cacheRepository.get(userCache,key);
            if (data == null ) {
                data = userDao.retrieveByUsername(username);
                if (data.getData() != null) {
                    this.cacheRepository.put(userCache,key,data);
                }
            }
            return data;
        } catch (Exception ex) {
         logger.error(ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<User> retrieveAll(int pageNumber, int pageSize) {

        try {
            String key = "user_getAllUsers_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.retrieveAll(pageNumber,pageSize);
                if (data.getList() != null) {
                    this.cacheRepository.put(userCache,key,data);
                }
            }
            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public Result<User> follow(long userId, long fanId) {

        try {
            return userDao.follow(userId,fanId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    public void unfollow(long userId, long fanId) {

        try {
            userDao.unfollow(userId,fanId);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<User> getFollowers(long id, int pageNumber, int pageSize) {

        try {
            String key = "user_getFollowers_id_"+id+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.getFollowers(id,pageNumber,pageSize);
                if (data.getList() != null) {
                    cacheRepository.put(userCache,key,data);
                }
            }

            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> getFollowing(long id, int pageNumber, int pageSize) {

        try {
            String key = "user_getFollowing_id_"+id+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.getFollowing(id,pageNumber,pageSize);
                if (data.getList() != null) {
                    cacheRepository.put(userCache,key,data);
                }
            }

            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> isFollower(long userId, long fanId) {

        try {
            String key = "user_isFollower_userId_"+userId+"_fanId_"+fanId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
               data = userDao.isFollower(userId,fanId);
               if (data.getData() != null) {
                   cacheRepository.put(userCache,key,data);
               }
            }

            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> isFollowing(long userId, long fanId) {

        try {
            String key = "user_isFollowing_userId_"+userId+"_fanId_"+fanId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.isFollowing(userId,fanId);
                if (data.getData() != null) {
                    cacheRepository.put(userCache,key,data);
                }
            }

            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public void updateUsername(String email, String username) {
        try {
            userDao.updateUsername(email,username);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void updatePhone(String email, String phone) {

        try {
            userDao.updatePhone(email,phone);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void updateEmail(long id, String email) {

        try {
            userDao.updateEmail(id,email);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void updateNationality(String email, String country) {

        try {
            userDao.updateNationality(email,country);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateTwoFactor(String email, boolean status) {

        try {
            userDao.updateTwoFactor(email,status);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updatePasswordResetProtection(String email, boolean status) {

        try {
            userDao.updatePasswordResetProtection(email,status);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateActivationStatus(String email, boolean status) {

        try {
            userDao.updateActivationStatus(email,status);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void update(User user) {

        try {
            userDao.update(user);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<User> getOldPasswords(long id) {

        try {
            String key = "user_getOldPasswords_id_"+id+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.retrieveOldPasswords(id);
                if (data != null) {
                    cacheRepository.put(userCache,key,data);
                }
            }
            return data;
        } catch (Exception ex) {
         logger.error(ex.getMessage());
        }

        return null;
    }

    public Result<User> updatePassword(String email, String password) {
        try {
            userDao.updatePassword(email,password);
            Result<User> result = userDao.retrieveByEmail(email);
            UserPrincipal userPrincipal = new UserPrincipal(result.getData());
            String accessToken = SystemUtils.getAccessToken();
            tokenService.remove(accessToken);
            setAuthentication(accessToken,userPrincipal);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    private void delete(String email) {

        try {
            userDao.delete(email);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public Result<User> suspendUser(long id, Date suspensionEndDate) {

        try {
            return userDao.suspendUser(id,suspensionEndDate);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> retrieveAllSuspendedUsers(int pageNumber, int pageSize) {

        try {
            String key = "user_retrieveAllSuspendedUsers_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.retrieveAllSuspendedUsers(pageNumber,pageSize);
                if (data != null) {
                    cacheRepository.put(userCache,key,data);
                }
            }

            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public Result<User> unsuspendUser(long userId) {

        try {
            return userDao.unsuspendUser(userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<User> retrieveAllLockedUsers(int pageNumber, int pageSize) {

        try {
            String key = "user_retrieveAllLockedUsers_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.retrieveAllLockedUsers(pageNumber,pageSize);
                if (data != null) {
                    cacheRepository.put(userCache,key,data);
                }
            }

            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public Result<User> unlockUserById(long userId) {

        try {
            return userDao.unlockUserById(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public void logout(String accessToken) {
        try {
            if (tokenService.contains(accessToken)){
                tokenService.remove(accessToken);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void clearFromRedisCache() {
        String key = "user_*";
        this.cacheRepository.clear(userCache,key);
    }


}
