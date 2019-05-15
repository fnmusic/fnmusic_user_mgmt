package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.model.Signup;
import org.springframework.stereotype.Service;

@Service
public interface IAuthDao<T> {

    public Result<T> createUser (Signup model);

    public void increaseUserAccessFailedCount(String email);

}
