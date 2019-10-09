package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

import java.util.Date;

public interface IUserDao<T> {

    Result<T> create(T object);

    Result<T> increaseLoginAttempt(String email, Date lockOutEndDate);

    Result<T> retrieveById(long id);

    Result<T> retrieveByEmail(String email);

    Result<T> retrieveByUsername(String username);

    Result<T> updateUsername(long id, String username);

    Result<T> updatePhone(long id, String phone);

    Result<T> updatePhoneConfirmed(long id, boolean status);

    Result<T> updateEmail(long id, String email);

    Result<T> updateEmailConfirmed(long id, boolean status);

    Result<T> updatePassword(long id, String password);

    Result<T> updateTwoFactor(long id, boolean status);

    Result<T> updatePasswordResetProtection(long id, boolean status);

    Result<T> updateNationality(long id, String country);

    Result<T> updateActivationStatus(long id, boolean status, Date dateDeleted);

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

    Result<T> delete(long id);

}
