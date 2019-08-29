package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

public interface IPermissionDao<T> {

    Result<T> create(T model);

    Result<T> retrieveById(int id);

    Result<T> retrieveByName(String name);

    Result<T> retrieveByFeature(String feature, int pageNumber, int pageSize);

    Result<T> retrieveAll(int pageNumber, int pageSize);

    Result<T> update(T model);

    Result<T> delete(int id);

}
