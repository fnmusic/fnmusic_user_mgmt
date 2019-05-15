package com.fnmusic.user.management.service;

import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.dao.impl.AuthDaoImpl;
import com.fnmusic.user.management.model.PasswordReset;
import com.fnmusic.user.management.model.User;
import com.fnmusic.user.management.model.Signup;
import com.fnmusic.user.management.redis.RedisCacheRepository;
import com.fnmusic.user.management.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    public Result<User> create(Signup signup) {
        Result<User> result = null;
        try {
            if (signup == null) {
                throw new IllegalArgumentException("Signup Object cannot be null");
            }
            result = authDao.createUser(signup);
            if (result.getIdentityValue() == null) {
                throw new IllegalStateException("Sorry, we couldn't create your account at this time, Try again later...");
            }

            User user = new User();
            user.setId(result.getIdentityValue());
            user.setFirstname(signup.getFirstname());
            user.setLastname(signup.getLastname());
            user.setUsername(signup.getUsername());
            user.setGender(signup.getGender());
            user.setEmail(signup.getEmail());
            user.setRole("user");

            result.setData(user);
            String userAccessToken = tokenService.generateUserAccessToken();
            String key = "user_" + userAccessToken;
            redisCacheRepository.put(authCache,key,user);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public void submitAccountActivationToken(String email, String accountActivationToken) {

        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (accountActivationToken.isEmpty()) {
            throw new IllegalArgumentException("Account Activation cannot be empty");
        }

        try {
            authDao.submitAccountActivationToken(email,accountActivationToken);
            String key = "auth_activationToken_email_"+email+"";
            redisCacheRepository.put(authCache,key,accountActivationToken);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public String getAccountActivationToken(String email) {

        if (email.equals(null) || email.isEmpty()) {
            throw new IllegalArgumentException("invalid email");
        }

        try {
            String key = "auth_activationToken_email_"+email+"";
            String token = (String) redisCacheRepository.get(authCache,key);
            if (token != null) {
                return token;
            } else {
                token = authDao.retrieveAccountActivationToken(email);
                if (token != null) {
                    redisCacheRepository.put(authCache,key,token);
                    return token;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public void increaseAccessFailedCount(String email) {

        if (email == null)
            throw new IllegalArgumentException("email cannot be null");

        try {
            authDao.increaseUserAccessFailedCount(email);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public void submitPasswordResetToken(String email, String passwordResetToken) {

        if (email.equals(null) || email.isEmpty()) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (passwordResetToken.equals(null) || passwordResetToken.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        try {
            authDao.submitPasswordResetToken(email,passwordResetToken);
            String key = "auth_passwordResetToken_email_"+email+"";
            redisCacheRepository.put(authCache,key,passwordResetToken);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String retrievePasswordResetToken(String email) {

        return null;
    }

    public void resetPassword(PasswordReset reset) {

        if (reset == null) {
            throw new IllegalArgumentException("Password reset object cannot be null");
        }

        try {
            authDao.resetPassword(reset);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void clearFromRedisCache() {
        String key = "user_*";
        redisCacheRepository.remove(authCache,key);
    }



}
