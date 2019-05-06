package com.fnmusic.user.management.dao;

import com.fnmusic.user.management.model.Signup;
import org.springframework.stereotype.Service;

@Service
public interface IAuthDao<T> {

    public Long create (Signup model);

    public void increaseAccessFailedCount(String email);

}
