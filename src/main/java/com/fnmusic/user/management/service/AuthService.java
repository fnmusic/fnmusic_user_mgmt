package com.fnmusic.user.management.service;

import com.fnmusic.user.management.dao.impl.AuthDaoImpl;
import com.fnmusic.user.management.model.PasswordReset;
import com.fnmusic.user.management.model.User;
import com.fnmusic.user.management.model.Signup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthDaoImpl authDao;

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    public User create(Signup signup) {

        if (signup == null) {
            throw new IllegalArgumentException("Signup Object cannot be null");
        }

        try {
            Long id = authDao.create(signup);
            if (id == null) {
                throw new IllegalStateException("Sorry, we couldn't create your account at this time, Try again later...");
            }

            User user = new User();
            user.setId(id);
            user.setFirstname(signup.getFirstname());
            user.setLastname(signup.getLastname());
            user.setUsername(signup.getUsername());
            user.setGender(signup.getGender());
            user.setEmail(signup.getEmail());
            user.setRole("user");

            return user;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public void submitAccountActivationToken(String email, String accountActivationToken) {
    }

    public void increaseAccessFailedCount(String email) {
        if (email == null)
            throw new IllegalArgumentException("email cannot be null");

        authDao.increaseAccessFailedCount(email);
    }

    public void submitPasswordResetToken(String email, String passwordResetToken) {

        try {
            authDao.submitPasswordResetToken(email,passwordResetToken);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
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



}
