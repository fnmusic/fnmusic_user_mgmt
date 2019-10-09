package com.fnmusic.user.management.dao;

import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.models.Auth;

public interface IAuthDao<T> {

    Result<T> submitLoginVerificationToken(T auth);

    Result<T> retrieveLoginVerificationToken(String email);

    Result<T> retrieveAllLoginVerificationTokens(int pageNumber, int pageSize);

    Result<T> deleteLoginVerificationToken(String email);

    Result<T> submitActivationToken(T auth);

    Result<T> retrieveActivationToken(String email);

    Result<T> retrieveAllActivationTokens(int pageNumber, int pageSize);

    Result<T> deleteActivationToken(String email);

    Result<T> activateAccount(T auth);

    Result<T> submitForgotPasswordVerificationToken(Auth auth);

    Result<T> retrieveForgotPasswordVerificationToken(String email);

    Result<T> retrieveAllForgotPasswordVerificationTokens(int pageNumber, int pageSize);

    Result<T> deleteForgotPasswordVerificationToken(String email);

    Result<T> submitPasswordResetToken(T auth);

    Result<T> retrievePasswordResetToken(String email);

    Result<T> retrieveAllPasswordResetTokens(int pageNumber, int pageSize);

    Result<T> deletePasswordResetToken(String email);

    Result<T> resetPassword(T auth);

    Result<T> submitPhoneVerificationToken(Auth auth);

    Result<T> retrievePhoneVerificationToken(String phone);

    Result<T> retrieveAllPhoneVerificationTokens(int pageNumber, int pageSize);

    Result<T> deletePhoneVerificationToken(String phone);

    Result<T> submitEmailVerificationToken(Auth auth);

    Result<T> retrieveEmailVerificationToken(String email);

    Result<T> retrieveAllEmailVerificationTokens(int pageNumber, int pageSize);

    Result<T> deleteEmailVerificationToken(String email);

    Result<T> submitTwoFactorVerificationToken(Auth auth);

    Result<T> retrieveTwoFactorVerificationToken(String phone);

    Result<T> retrieveAllTwoFactorVerificationTokens(int pageNumber, int pageSize);

    Result<T> deleteTwoFactorVerificationToken(String phone);

}
