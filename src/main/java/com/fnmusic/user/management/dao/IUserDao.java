package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;

import java.util.Date;

public interface IUserDao<T> {

    Result<T> create(T object);

    Result<T> increaseLoginAttempt(String email, Date lockOutEndDate);

    Result<T> retrieveById(long userId);

    Result<T> retrieveByEmail(String email);

    Result<T> retrieveByUsername(String username);

    Result<T> retrieveByPhone(String phone);

    Result<T> updateUsername(long userId, String username);

    Result<T> updatePhone(long userId, String phone);

    Result<T> updatePhoneConfirmed(long userId, boolean status);

    Result<T> updateEmail(long userId, String email);

    Result<T> updateEmailConfirmed(long userId, boolean status);

    Result<T> updatePassword(long userId, String password);

    Result<T> updateTwoFactor(long userId, boolean status);

    Result<T> updatePasswordResetProtection(long userId, boolean status);

    Result<T> updateNationality(long userId, String country);

    Result<T> updateActivationStatus(long userId, boolean status, Date dateDeleted);

    Result<T> update(T object);

    Result<T> retrieveOldPasswords(long userId);

    Result<T> follow(long userId, long followerId);

    Result<T> unfollow(long userId, long followerId);

    Result<T> getFollowers(long userId, int pageNumber, int pageSize);

    Result<T> getFollowing(long userId, int pageNumber, int pageSize);

    Result<T> isFollower(long userId, long followerId);

    Result<T> isFollowing(long userId, long followerId);

    Result<T> suspendUser(long userId, Date suspensionEndDate);

    Result<T> unsuspendUser(long userId);

    Result<T> delete(long userId);

}
