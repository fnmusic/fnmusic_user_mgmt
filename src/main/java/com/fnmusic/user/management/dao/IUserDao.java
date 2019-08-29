package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

import java.util.Date;

public interface IUserDao<T> {

    Result<T> create(T object);

    Result<T> increaseLoginAttempt(String email, Date lockOutEndDate);

    Result<T> retrieveById(long id);

    Result<T> retrieveByEmail(String email);

    Result<T> retrieveByUsername(String username);

    Result<T> updateUsername(String email, String username);

    Result<T> updatePhone(String email, String phone);

    Result<T> updateEmail(long id, String email);

    Result<T> updatePassword(String email, String password);

    Result<T> updateTwoFactor(String email, boolean status);

    Result<T> updatePasswordResetProtection(String email, boolean status);

    Result<T> updateNationality(String email, String country);

    Result<T> updateActivationStatus(String email, boolean status);

    Result<T> update(T object);

    Result<T> retrieveOldPasswords(long userId);

    Result<T> follow(long userId, long fanId);

    Result<T> unfollow(long userId, long fanId);

    Result<T> getFollowers(long userId, int pageNumber, int pageSize);

    Result<T> getFollowing(long userId, int pageNumber, int pageSize);

    Result<T> isFollower(long userId, long fanId);

    Result<T> isFollowing(long userId, long fanId);

    Result<T> suspendUser(long userId, Date suspensionEndDate);

    Result<T> unsuspendUser(long userId);

    Result<T> delete(String email);

}
