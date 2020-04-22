package com.fnmusic.user.management.services;

import com.fnmusic.base.models.*;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.base.security.AuthenticationWithToken;
import com.fnmusic.base.utils.CharacterType;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.base.utils.RandomGeneratorUtils;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.dao.impl.UserDao;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

    private static Object userCache;
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostConstruct
    public void init() {
        this.userCache = cacheRepository.createCache(ConstantUtils.APPNAME,"userCache",Long.MAX_VALUE);
        createSuperAdmin();
        clearFromRedisCache();
    }

    void createSuperAdmin() {

        try {
            Result<User> byUsername = retrieveUserByUsername("superadmin");
            if (byUsername.getData() == null) {
                User superAdmin = new User();
                superAdmin.setEmail("superadmin@fnmusic.com");
                superAdmin.setUsername("superadmin");
                superAdmin.setName("Super Admin");
                superAdmin.setPasswordHash(hashService.encode("password123"));
                superAdmin.setRole(Role.SUPER_ADMIN);
                superAdmin.setDateCreated(new Date());
                create(superAdmin);
            }
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

            String accessToken = RandomGeneratorUtils.generateCode(CharacterType.ALPHABETIC,120).toLowerCase();

            //Still to be Implemented
            String refreshToken = RandomGeneratorUtils.generateCode(CharacterType.ALPHANUMERIC,200).toLowerCase();

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
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE) + 2);
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
    public Result<User> retrieveUserByPhone(String phone) {
        try {
            String key = "user_findUserByPhone_phone_"+phone+"";
            Result<User> data = (Result<User>) this.cacheRepository.get(userCache,key);
            if (data == null ) {
                data = userDao.retrieveByPhone(phone);
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

    public Result<User> follow(long userId, long followerId) {

        try {
            return userDao.follow(userId,followerId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            clearFromRedisCache();
        }

        return null;
    }

    public void unfollow(long userId, long followerId) {

        try {
            userDao.unfollow(userId,followerId);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<User> getFollowers(long userId, int pageNumber, int pageSize) {

        try {
            String key = "user_getFollowers_userId_"+userId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.getFollowers(userId,pageNumber,pageSize);
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
    public Result<User> getFollowing(long userId, int pageNumber, int pageSize) {

        try {
            String key = "user_getFollowing_userId_"+userId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.getFollowing(userId,pageNumber,pageSize);
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
    public Result<User> isFollower(long userId, long followerId) {

        try {
            String key = "user_isFollower_userId_"+userId+"_followerId_"+followerId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
               data = userDao.isFollower(userId,followerId);
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
    public Result<User> isFollowing(long userId, long followerId) {
        try {
            String key = "user_isFollowing_userId_"+userId+"_followerId_"+followerId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.isFollowing(userId,followerId);
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

    public void updateUsername(long userId, String username) {
        try {
            userDao.updateUsername(userId,username);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void updatePhone(long userId, String phone) {
        try {
            userDao.updatePhone(userId,phone);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void updatePhoneConfirmed(long userId, boolean status) {
        try {
            userDao.updatePhoneConfirmed(userId,status);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateEmail(long userId, String email) {
        try {
            userDao.updateEmail(userId,email);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void updateEmailConfirmed(long userId, boolean status) {
        try {
            userDao.updateEmailConfirmed(userId,status);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateNationality(long userId, String country) {
        try {
            userDao.updateNationality(userId,country);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateTwoFactor(long userId, boolean status) {
        try {
            userDao.updateTwoFactor(userId,status);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updatePasswordResetProtection(long userId, boolean status) {
        try {
            userDao.updatePasswordResetProtection(userId,status);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateActivationStatus(long userId, boolean status, Date dateDeleted) {
        try {
            userDao.updateActivationStatus(userId,status,dateDeleted);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void update(User user) {

        try {
            userDao.update(user);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<User> getOldPasswords(long userId) {

        try {
            String key = "user_getOldPasswords_id_"+userId+"";
            Result<User> data = (Result<User>) cacheRepository.get(userCache,key);
            if (data == null) {
                data = userDao.retrieveOldPasswords(userId);
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

    public void updatePassword(long userId, String password) {
        try {
            userDao.updatePassword(userId,password);
            updateAuthentication();
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void updateAuthentication() {
        Result<User> result = userDao.retrieveById(SystemUtils.getCurrentUser().getId());
        UserPrincipal userPrincipal = new UserPrincipal(result.getData());
        String accessToken = SystemUtils.getAccessToken();
        tokenService.remove(accessToken);
        setAuthentication(accessToken,userPrincipal);
    }

    private void delete(long userId) {

        try {
            userDao.delete(userId);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public Result<User> suspendUser(long userId, Date suspensionEndDate) {

        try {
            return userDao.suspendUser(userId,suspensionEndDate);
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

    boolean isAfterCurrentTime(Date date) {
        Date currentTime = new Date();
        if (!currentTime.after(date)) {
            return false;
        }

        return true;
    }

    @Scheduled(fixedRate = 3600000)
    void serviceCheck() {
        int pageSize = 100;

        //locked accounts
        for (int pageNumber = 1; pageNumber < 100; pageNumber++) {
            Result<User> result = userDao.retrieveAllLockedUsers(pageNumber,pageSize);
            if (!result.getList().isEmpty()) {
                List<User> list = result.getList();
                if (!list.isEmpty()) {
                    for (User user : list) {
                        //checked for lock out end date expiry data
                        if (isAfterCurrentTime(user.getLockOutEndDateUtc())) {
                            unlockUserById(user.getId());
                        }
                    }
                }
            }
        }

    }
}
