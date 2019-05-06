package com.fnmusic.user.management.dao;

public interface IUserDao<T> {

    public T retrieveUserByEmail(String email);

    public T retrieveUserByUsername(String username);

    public T retrieveUserById(Long id);
}
