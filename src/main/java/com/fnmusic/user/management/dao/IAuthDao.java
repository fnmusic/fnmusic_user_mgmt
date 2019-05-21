package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

public interface IAuthDao<T> {

    public Result<T> increaseLoginAttempt(String email);

    public Result<T> submitActivationToken(T model);
//
//    public Result<T> retrieveActivationToken(String email);
//
//    public Result<T> activateAccount(T model);
//
//    public Result<T> submitPasswordResetToken(T model);
//
//    public Result<T> retrievePasswordResetToken(String email);
//
//    public Result<T> resetPassword(T model);

}
