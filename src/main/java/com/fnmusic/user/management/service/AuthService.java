package com.fnmusic.user.management.service;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.user.management.dao.impl.AuthDaoImpl;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import com.fnmusic.user.management.models.UserAuth;
import com.fnmusic.user.management.redis.RedisCacheRepository;
import com.fnmusic.user.management.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Service
public class AuthService {

    @Autowired
    private AuthDaoImpl authDao;
    @Autowired
    private RedisCacheRepository redisCacheRepository;
    @Value("${app.redisTtl}")
    private long redisTtl;
    @Autowired
    private TokenService tokenService;

    private static final String KEY = "user";
    private static Object authCache;
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @PostConstruct
    public void init() {
        authCache = redisCacheRepository.createCache(Utils.APPNAME,"authCache",redisTtl);
    }

    @Validated
    public Result<UserAuth> increaseLoginAttempt(@NotNull @NotEmpty String email) {

        try {
            Result<UserAuth> result = authDao.increaseLoginAttempt(email);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }


    @Validated
    public Result<UserAuth> generateActivationToken(@NotNull @NotEmpty String email) {

        try {
            String token = tokenService.generateLinkToken(20);
            UserAuth userAuth = new UserAuth();
            userAuth.setEmail(email);
            userAuth.setToken(token);
            Result<UserAuth> result = authDao.submitActivationToken(userAuth);
            if (result.getStatus()) {
                String key = "auth_activationToken_email_"+email+"";
                redisCacheRepository.put(authCache,key,token);
            }

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

//    @Validated
//    public Result<UserAuth> retrieveActivationToken(@NotNull @NotEmpty String email) {
//
//        try {
//            Result<UserAuth> result = null;
//            String key = "auth_activationToken_email_"+email+"";
//            String token = (String) redisCacheRepository.get(authCache,key);
//            if (token != null) {
//                result = new Result<>();
//                UserAuth userAuth = new UserAuth();
//                userAuth.setEmail(email);
//                userAuth.setToken(token);
//                result.setData(userAuth);
//
//                return result;
//            } else {
//                result = authDao.retrieveActivationToken(email);
//                if (result.getData().getToken() != null) {
//                    redisCacheRepository.put(authCache,key,token);
//
//                }
//
//                return result;
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//        return null;
//    }
//
//    public boolean activateAccount(String email) {
//
//        try {
//            clearFromRedisCache();
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//        return false;
//    }
//
//    @Validated
//    public Result<UserAuth> generatePasswordResetToken(@NotNull @NotEmpty String email) {
//
//        try {
//            String token = tokenService.generateLinkToken(15);
//            if (token.isEmpty() || token == null) {
//                throw new InternalServerErrorException("Something went wrong, please try again later");
//            }
//
//            boolean taskCompleted = authDao.submitPasswordResetToken(email,token);
//            if (taskCompleted) {
//                String key = "auth_passwordResetToken_email_"+email+"";
//                redisCacheRepository.put(authCache,key,token);
//            }
//
//            return token;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//        return null;
//    }
//
//    @Validated
//    public Result<UserAuth> retrievePasswordResetToken(@NotNull @NotEmpty String email) {
//
//        try {
//            String token = authDao.retrievePasswordResetToken(email);
//            if (token.isEmpty() || token == null) {
//                throw new InternalServerErrorException("Something went wrong, please try again later");
//            }
//
//            return token;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//        return null;
//    }
//
////    public boolean resetPassword(PasswordReset reset) {
////
////        if (reset == null) {
////            throw new IllegalArgumentException("Password reset object cannot be null");
////        }
////
////        try {
////            User user = new User();
////            user.setEmail(reset.getEmail());
////            user.setPasswordHash(reset.getPassword());
////            boolean isReset = authDao.resetPassword(user);
////            return isReset;
////        } catch (Exception ex) {
////            logger.error(ex.getMessage());
////        }
////
////        return false;
////    }
//
//    public void clearFromRedisCache() {
//        String key = "auth_*";
//        redisCacheRepository.remove(authCache,key);
//    }

}
