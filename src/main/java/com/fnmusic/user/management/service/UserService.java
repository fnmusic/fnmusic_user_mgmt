package com.fnmusic.user.management.service;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.models.UserPrincipal;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.base.security.AuthenticationWithToken;
import com.fnmusic.user.management.dao.impl.UserDaoImpl;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Service
public class UserService {

    @Autowired
    private UserDaoImpl userDao;
    @Autowired
    private TokenService tokenService;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    public Result<User> createUser(User user) {
        String newToken = null;
        try {

            Result<User> result = userDao.create(user);
            if (result.getIdentityValue() == null) {
                throw new InternalServerErrorException("Sorry, we couldn't create your account at this time, Try again later...");
            }
            user.setId(result.getIdentityValue());
            result.setData(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);
            AuthenticationWithToken authWithToken =new AuthenticationWithToken(userPrincipal, null, userPrincipal.getAuthorities());
            newToken = tokenService.generateUserAccessToken();
            authWithToken.setToken(newToken);
            tokenService.store(newToken,authWithToken);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public AccessTokenWithUserDetails loginUser(User user) {

        try {
            UserPrincipal userPrincipal = new UserPrincipal(user);
            AuthenticationWithToken authWithToken = new AuthenticationWithToken(userPrincipal,null,userPrincipal.getAuthorities());
            String newToken = tokenService.generateUserAccessToken();
            authWithToken.setToken(newToken);
            authWithToken.setDetails(newToken);
            tokenService.store(newToken,authWithToken);
            SecurityContextHolder.getContext().setAuthentication(authWithToken);

            AccessTokenWithUserDetails access = new AccessTokenWithUserDetails();
            access.setAccessToken(newToken);
            access.setUsername(user.getEmail());
            access.setUser(user);

            return access;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public Result<User> findUserById(Long id) {

        if (id == 0) {
            throw new IllegalArgumentException("invalid user id");
        }

        try {
            Result<User> result = userDao.retrieveById(id);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @Validated
    public Result<User> findUserByEmail(@NotNull @NotEmpty String email) {

        try {
            Result<User> result = userDao.retrieveByEmail(email);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public Result<User> findUserByUsername(String username) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("username cannot be null or empty");

        Result<User> result = userDao.retrieveByUsername(username);
        return result;
    }

    public Result<User> update(User user) {
        return null;
    }

    public void delete(String email) {

    }
}
