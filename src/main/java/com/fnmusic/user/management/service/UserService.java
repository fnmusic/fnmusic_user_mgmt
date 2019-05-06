package com.fnmusic.user.management.service;

import com.fnmusic.user.management.dao.impl.UserDaoImpl;
import com.fnmusic.user.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDaoImpl userDaoImpl;

    public User findUserByEmail(String email) {

        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("email cannot be null or empty");

        User user = userDaoImpl.retrieveUserByEmail(email);
        return user;
    }

    public User findUserByUsername(String username) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("username cannot be null or empty");

        User user = userDaoImpl.retrieveUserByUsername(username);
        return user;
    }


    public User findUserById(Long id) {
        if (id == 0)
            throw new IllegalArgumentException("invalid user id");

        User user = userDaoImpl.retrieveUserById(id);
        return user;
    }
}
