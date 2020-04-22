package com.fnmusic.user.management.services;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.dao.impl.AuthDao;
import com.fnmusic.user.management.models.Auth;
import com.fnmusic.user.management.repository.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private CacheRepository cacheRepository;
    @Value("${app.redisTtl}")
    private Long redisTtl;

    private static Object authCache;
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @PostConstruct
    public void init() {
        authCache = cacheRepository.createCache(ConstantUtils.APPNAME,"authCache",redisTtl);
        clearFromRedisCache();
    }

    public void submitLoginVerificationToken(Auth auth) {

        try {
            authDao.submitLoginVerificationToken(auth);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrieveLoginVerificationToken(String email) {

        try {
            String key = "auth_retrieveLoginVerificationToken_email_"+email+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveLoginVerificationToken(email);
                if (data.getData() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<Auth> retrieveAllLoginVerificationTokens(int pageNumber, int pageSize) {

        try {
            String key = "auth_retrieveAllLoginVerificationTokens_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllLoginVerificationTokens(pageNumber,pageSize);
                if (data.getList() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private void deleteLoginVerificationToken(String email) {

        try {
            authDao.deleteLoginVerificationToken(email);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void submitActivationToken(Auth auth) {

        try {
            authDao.submitActivationToken(auth);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrieveActivationToken(String email) {

        try {
            String key = "auth_retrieveActivationToken_email_"+email+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveActivationToken(email);
                if (data != null) {
                    this.cacheRepository.put(authCache,key,data);
                }
            }

            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<Auth> retrieveAllActivationTokens(int pageNumber, int pageSize) {

        try {
            String key = "auth_retrieveAllActivationTokens_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllActivationTokens(pageNumber,pageSize);
                if (data.getList() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private void deleteActivationToken(String email) {

        try {
            authDao.deleteActivationToken(email);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void activateAccount(Auth auth) {

        try {
            Result<Auth> result = authDao.activateAccount(auth);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void submitForgotPasswordVerificationToken(@NotNull Auth auth) {
        try {
            authDao.submitForgotPasswordVerificationToken(auth);
            clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrieveForgotPasswordVerificationToken(@Email String email) {
        try{
            String key = "auth_retrieveForgotPasswordVerificationToken_email_"+email+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveForgotPasswordVerificationToken(email);
                if (data.getData() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<Auth> retrieveAllForgotPasswordVerificationTokens(int pageNumber, int pageSize) {

        try {
            String key = "auth_retrieveAllForgotPasswordVerificationTokens_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllForgotPasswordVerificationTokens(pageNumber, pageSize);
                if (data.getList() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private void deleteForgotPasswordVerificationToken(String email) {

        try {
            authDao.deleteForgotPasswordVerificationToken(email);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void generatePasswordResetToken(@NotNull Auth auth) {

        try {
            authDao.submitPasswordResetToken(auth);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrievePasswordResetToken(@Email String email) {

        try {
            String key = "auth_retrievePasswordResetToken_email_"+email+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrievePasswordResetToken(email);
                if (data != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<Auth> retrieveAllPasswordResetTokens(int pageNumber, int pageSize) {

        try {
            String key = "auth_retrieveAllPasswordResetTokens_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllPasswordResetTokens(pageNumber, pageSize);
                if (data.getList() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public void resetPassword(Auth auth) {

        try {
            authDao.resetPassword(auth);
            clearFromRedisCache();
            userService.clearFromRedisCache();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void deletePasswordResetToken(String email) {

        try {
            authDao.deletePasswordResetToken(email);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void submitPhoneVerificationToken(Auth auth) {
        try {
            authDao.submitPhoneVerificationToken(auth);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrievePhoneVerificationToken(String phone) {
        try {
            String key = "auth_retrievePhoneVerificationToken_phone_"+phone+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrievePhoneVerificationToken(phone);
                if (data.getData() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<Auth> retrieveAllPhoneVerificationTokens(int pageNumber, int pageSize) {
        try {
            String key = "auth_retrieveAllPhoneVerificationToken_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllPhoneVerificationTokens(pageNumber,pageSize);
                if (!data.getList().isEmpty()) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private void deletePhoneVerificationToken(String phone) {
        try {
            authDao.deletePhoneVerificationToken(phone);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void submitEmailVerificationToken(Auth auth) {
        try {
            authDao.submitEmailVerificationToken(auth);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrieveEmailVerificationToken(String email) {
        try {
            String key = "auth_retrieveEmailVerificationToken_email_"+email+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveEmailVerificationToken(email);
                if (data.getData() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Result<Auth> retrieveAllEmailVerificationTokens(int pageNumber, int pageSize) {
        try {
            String key = "auth_retrieveAllEmailVerificationTokens_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllEmailVerificationTokens(pageNumber,pageSize);
                if (!data.getList().isEmpty()) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private void deleteEmailVerificationToken(String email) {
        try {
            authDao.deleteEmailVerificationToken(email);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void submitTwoFactorVerificationToken(Auth auth) {
        try {
            authDao.submitTwoFactorVerificationToken(auth);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrieveTwoFactorVerificationToken(String phone) {
        try {
            String key = "auth_retrieveTwoFactorVerificationToken_phone_"+phone+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveTwoFactorVerificationToken(phone);
                if (data.getData() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Result<Auth> retrieveAllTwoFactorVerificationTokens(int pageNumber, int pageSize) {
        try {
            String key = "auth_retrieveAllTwoFactorVerificationTokens_pageNumber_"+pageNumber+"_pageSize_"+pageSize+"";
            Result<Auth> data = (Result<Auth>) cacheRepository.get(authCache,key);
            if (data == null) {
                data = authDao.retrieveAllTwoFactorVerificationTokens(pageNumber,pageSize);
                if (data.getData() != null) {
                    cacheRepository.put(authCache,key,data);
                }
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private void deleteTwoFactorVerificationToken(String phone) {
        try {
            authDao.deleteTwoFactorVerificationToken(phone);
            clearFromRedisCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void clearFromRedisCache() {
        String key = "auth_*";
        cacheRepository.clear(authCache,key);
    }

    boolean isAfterCurrentTime(Date date) {
        Date currentTime = new Date();
        if (!currentTime.after(date)) {
            return false;
        }

        return true;
    }

    @Scheduled(fixedRate = 3600000)
    private void ServiceCheck() {
        int pageSize = 100;

        //Login Verification Tokens
        for (int pageNumber = 1; pageNumber < 100; pageNumber++) {
            Result<Auth> result = retrieveAllLoginVerificationTokens(pageNumber,pageSize);
            if (!result.getList().isEmpty()) {
                List<Auth> list = result.getList();
                if (!list.isEmpty()) {
                    for (Auth auth : list) {
                        //check for expired tokens
                        if (isAfterCurrentTime(auth.getExpiryDate())) {
                            deleteLoginVerificationToken(auth.getEmail());
                        }
                    }
                }
            } else {
                break;
            }
        }

        //Activation Tokens
        for (int pageNumber = 1; pageNumber < 100; pageNumber++) {
            Result<Auth> result = retrieveAllActivationTokens(pageNumber,pageSize);
            if (!result.getList().isEmpty()) {
                List<Auth> list = result.getList();
                if (!list.isEmpty()) {
                    for (Auth auth : list) {
                        //check for expired tokens
                        if (isAfterCurrentTime(auth.getExpiryDate())) {
                            deleteActivationToken(auth.getEmail());
                        }
                    }
                }
            } else {
                break;
            }
        }

        //Forgot Password Verification Tokens
        for (int pageNumber = 1; pageNumber < 100; pageNumber++) {
            Result<Auth> result = retrieveAllForgotPasswordVerificationTokens(pageNumber,pageSize);
            if (!result.getList().isEmpty()) {
                List<Auth> list = result.getList();
                if (!list.isEmpty()) {
                    for (Auth auth : list) {
                        //check for expired tokens
                        if (isAfterCurrentTime(auth.getExpiryDate())) {
                            deleteForgotPasswordVerificationToken(auth.getEmail());
                        }
                    }
                }
            } else {
                break;
            }
        }

        //Password Reset Tokens
        for (int pageNumber = 1; pageNumber < 100; pageNumber++) {
            Result<Auth> result = retrieveAllPasswordResetTokens(pageNumber,pageSize);
            if (!result.getList().isEmpty()) {
                List<Auth> list = result.getList();
                if (!list.isEmpty()) {
                    for (Auth auth : list) {
                        //check for expired tokens
                        if (isAfterCurrentTime(auth.getExpiryDate())) {
                            deletePasswordResetToken(auth.getEmail());
                        }
                    }
                }
            } else {
                break;
            }
        }

    //end
    }

}
