package com.fnmusic.user.management.tests.service;

import com.fnmusic.base.models.User;
import com.fnmusic.user.management.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.annotation.PostConstruct;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {

    }

    @Test
    public void shouldFailifUserObjectIsNull() {
        try {
            User user = null;
            userService.createUser(user);
        } catch (Exception e) {
            assertEquals(e.getMessage(), MethodArgumentNotValidException.class,e.getClass());
        }


    }


}
