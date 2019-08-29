package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;

public interface IFeatureDao<T> {

    Result<T> create(T model);

    Result<T> retrieveById(int id);

    Result<T> retrieveByName(String name);

    Result<T> retrieveByRole(String role);

    Result<T> retrieveAll(int pageNumber, int pageSize);

    Result<T> update(T model);

    Result<T> delete(int id);

}
