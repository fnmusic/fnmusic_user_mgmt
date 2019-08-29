package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

public interface IRoleDao<T> {

    Result<T> create(T model);

    Result<T> retrieveById(int id);

    Result<T> retrieveByName(String name);

    Result<T> retrieveAll(int pageNumber, int pageSize);

    Result<T> update(T model);

    Result<T> deleteByUniqueId(int id);

}
