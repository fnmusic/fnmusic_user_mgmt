package com.fnmusic.user.management.services;

import com.fnmusic.base.models.RedisCacheConfig;
import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.dao.impl.AuthDao;
import com.fnmusic.user.management.models.Auth;
import com.fnmusic.user.management.repository.CacheRepository;
import com.fnmusic.user.management.utils.ModelUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private CacheRepository cacheRepository;
    @Mock
    private AuthDao authDao;
    @InjectMocks
    private AuthService authService;

    private String email;

    @Before
    public void init() {
        email = "johndoe@yahoo.com";

        when(authService.retrieveActivationToken(email))
                .thenReturn(new Result<>(0,ModelUtils.auth()));

    }

    @Test
    public void submitActivationToken() {
        try {
            authService.submitActivationToken(ModelUtils.auth());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void retrieveActivationTokenFromCache() {
        when(cacheRepository.get(any(RedisCacheConfig.class),any(String.class)))
                .thenReturn(new Result<>(0,ModelUtils.auth()));
        Result<Auth> result = authService.retrieveActivationToken(email);
        assertNotNull(result.getData());
    }

    @Test
    public void retrieveActivationTokenFromDB() {
        when(authDao.retrieveActivationToken(email))
                .thenReturn(new Result<>(0,ModelUtils.auth()));
        Result<Auth> result = authService.retrieveActivationToken(email);
        assertNotNull(result.getData());
    }

    @Test
    public void activate() {
        authService.activateAccount(ModelUtils.auth());
    }


}
