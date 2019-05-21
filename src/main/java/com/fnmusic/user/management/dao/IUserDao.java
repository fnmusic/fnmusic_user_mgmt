package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

public interface IUserDao<T> {

    public Result<T> create(T object);

    public Result<T> retrieveById(long id);

    public Result<T> retrieveByEmail(String email);

    public Result<T> retrieveByUsername(String username);

    public Result<T> update(T object);

    public Result<T> delete(String email);

}
