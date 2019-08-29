package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.user.management.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao extends AbstractBaseDao<User> implements IUserDao<User> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall uspIncreaseLoginAttempt;
    private SimpleJdbcCall uspUpdateUsername;
    private SimpleJdbcCall uspUpdateEmail;
    private SimpleJdbcCall uspUpdatePhone;
    private SimpleJdbcCall uspUpdateTwoFactor;
    private SimpleJdbcCall uspUpdatePasswordResetProtection;
    private SimpleJdbcCall uspUpdateNationality;
    private SimpleJdbcCall uspUpdateActivationStatus;
    private SimpleJdbcCall uspFollowUser;
    private SimpleJdbcCall uspUnfollowUser;
    private SimpleJdbcCall uspGetFollowers;
    private SimpleJdbcCall uspGetFollowing;
    private SimpleJdbcCall uspIsFollower;
    private SimpleJdbcCall uspIsFollowing;
    private SimpleJdbcCall uspRetrieveOldPasswords;
    private SimpleJdbcCall uspSuspendUser;
    private SimpleJdbcCall uspRetrieveAllSuspendedUsers;
    private SimpleJdbcCall uspUnsuspendUser;
    private SimpleJdbcCall uspRetrieveAllLockedUsers;
    private SimpleJdbcCall uspUnlockUserById;

    @Override
    public void init() {

        pspCreate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_create_user")
                .withReturnValue();

        pspRetrieveByUniqueId = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_user_by_id")
                .withReturnValue()
                .returningResultSet(DATA , BeanPropertyRowMapper.newInstance(User.class));

        pspRetrieveByUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_user_by_email")
                .withReturnValue()
                .returningResultSet(DATA , BeanPropertyRowMapper.newInstance(User.class));

        pspRetrieveBySecondUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_user_by_username")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(User.class));

        pspRetrieveAll = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_users")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(User.class));

        pspUpdate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_user")
                .withReturnValue();

        uspIncreaseLoginAttempt = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_increase_login_attempt")
                .withReturnValue();

        uspUpdateEmail = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_email")
                .withReturnValue();

        uspUpdatePhone = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_phone")
                .withReturnValue();

        uspUpdateUsername = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_username")
                .withReturnValue();

        uspUpdateNationality = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_nationality")
                .withReturnValue();

        uspUpdateActivationStatus = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_activation_status")
                .withReturnValue();

        uspUpdateTwoFactor = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_two_factor")
                .withReturnValue();

        uspUpdatePasswordResetProtection = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_password_reset_protection")
                .withReturnValue();

        uspRetrieveOldPasswords = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_old_passwords")
                .withReturnValue()
                .returningResultSet(LIST,BeanPropertyRowMapper.newInstance(User.class));

        pspUpdateByUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_update_password")
                .withReturnValue();

        uspFollowUser = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_follow_user")
                .withReturnValue();

        uspUnfollowUser = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_unfollow_user")
                .withReturnValue();

        uspGetFollowers = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_get_followers")
                .withReturnValue()
                .returningResultSet(LIST,BeanPropertyRowMapper.newInstance(User.class));

        uspGetFollowing = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_get_following")
                .withReturnValue()
                .returningResultSet(LIST,BeanPropertyRowMapper.newInstance(User.class));

        uspIsFollower = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_is_follower")
                .withReturnValue()
                .returningResultSet(DATA,BeanPropertyRowMapper.newInstance(User.class));

        uspIsFollowing = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_is_following")
                .withReturnValue()
                .returningResultSet(DATA,BeanPropertyRowMapper.newInstance(User.class));

        uspSuspendUser = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_suspend_user")
                .withReturnValue();

        uspRetrieveAllSuspendedUsers = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_suspended_users")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(User.class));

        uspUnsuspendUser = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_unsuspend_user")
                .withReturnValue();

        uspRetrieveAllLockedUsers = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_locked_users")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(User.class));

        uspUnlockUserById = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_unlock_user_by_id")
                .withReturnValue();

    }

    @Transactional
    @Override
    public Result<User> create(User object) {
        return super.create(object);
    }

    @Transactional
    @Override
    public Result<User> increaseLoginAttempt(String email, Date lockOutEndDate) {

        if (uspIncreaseLoginAttempt == null) {
            throw new IllegalStateException("uspIncreaseAccessFailedCount has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("lockOutEndDate",lockOutEndDate)
                .addValue("status",false);
        Map<String,Object> m = uspIncreaseLoginAttempt.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> retrieveById(long id) {
        return super.retrieveByUniqueId(id);
    }

    @Transactional
    @Override
    public Result<User> retrieveByEmail(String email) {
        return super.retrieveByUniqueKey(email);
    }

    @Transactional
    @Override
    public Result<User> retrieveByUsername(String username) {
        return super.retrieveBySecondUniqueKey(username);
    }

    @Transactional
    @Override
    public Result<User> retrieveAll(int pageNumber, int pageSize) {
        return super.retrieveAll(pageNumber, pageSize);
    }

    @Transactional
    @Override
    public Result<User> updateUsername(String email, String username) {

        if (uspUpdateUsername == null) {
            throw new IllegalStateException("uspUpdateUsername cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("username",username);
        Map<String,Object> m = uspUpdateUsername.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> updatePhone(String email, String phone) {

        if (uspUpdatePhone == null) {
            throw new IllegalStateException("uspUpdatePhone cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("phone",phone);
        Map<String,Object> m = uspUpdatePhone.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> updateEmail(long id, String email) {

        if (uspUpdateEmail == null) {
            throw new IllegalStateException("uspUpdatePhone cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id",id)
                .addValue("email",email);
        Map<String,Object> m = uspUpdateEmail.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> updateTwoFactor(String email, boolean enabled) {

        if (uspUpdateTwoFactor == null) {
            throw new IllegalStateException("uspUpdateTwoFactor cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("enabled",enabled);
        Map<String, Object> m = uspUpdateTwoFactor.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> updatePasswordResetProtection(String email, boolean enabled) {

        if (uspUpdatePasswordResetProtection == null) {
            throw new IllegalStateException("uspUpdateTwoFactor cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("enabled",enabled);
        Map<String, Object> m = uspUpdatePasswordResetProtection.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> updateNationality(String email, String country) {

        if (uspUpdateNationality == null) {
            throw new IllegalStateException("uspUpdateTwoFactor cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("country",country);
        Map<String, Object> m = uspUpdateNationality.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> updateActivationStatus(String email, boolean activated) {
        if (uspUpdateActivationStatus == null) {
            throw new IllegalStateException("uspUpdateTwoFactor cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("activated",activated);
        Map<String, Object> m = uspUpdateActivationStatus.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode);
    }

    @Transactional
    @Override
    public Result<User> update(User object) {
        return super.update(object);
    }

    @Transactional
    @Override
    public Result<User> updatePassword(String email, String password) {
        return super.updateByUniqueKey(email,password);
    }

    @Transactional
    @Override
    public Result<User> delete(String email) {
        return super.deleteByUniqueKey(email);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> retrieveOldPasswords(long userId) {
        if (uspRetrieveOldPasswords == null) {
            throw new IllegalStateException("uspRetrieveOldPasswords cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId",userId);
        Map<String,Object> m = uspRetrieveOldPasswords.execute(in);

        List<User> list = m.containsKey(LIST) ? (List<User>)m.get(LIST) : null;
        long noOfRecords = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0L;
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer)m.get(RETURN_VALUE) : 0;
        return new Result<User>(resultCode,list,noOfRecords,0,0);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> follow(long userId, long fanId) {
        if (uspFollowUser == null) {
            throw new IllegalStateException("uspFollowUser cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId",userId)
                .addValue("fanId",fanId);
        Map<String, Object> m = uspFollowUser.execute(in);

        long id = m.containsKey("id") ? (Long)m.get("id") : 0L;
        int resultCode = m.containsKey("RETURN_VALUE") ? (Integer)m.get("RETURN_VALUE") : 0;
        Result<User> result = new Result(resultCode, id);
        return result;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> unfollow(long userId, long fanId) {
        if (uspUnfollowUser == null) {
            throw new IllegalStateException("uspFollowUser cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId",userId)
                .addValue("fanId",fanId);
        Map<String, Object> m = uspUnfollowUser.execute(in);

        long id = m.containsKey(IDENTITY) ? (Long)m.get(IDENTITY) : 0L;
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer)m.get(RETURN_VALUE) : 0;
        Result<User> result = new Result(resultCode, id);
        return result;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> getFollowers(long userId, int pageNumber, int pageSize) {
        if (uspGetFollowers == null) {
            throw new IllegalStateException("uspGetFollowers cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id",userId)
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize);
        Map<String,Object> m = uspGetFollowers.execute(in);

        List<User> list = m.containsKey(LIST) ? (List<User>)m.get(LIST) : null;
        long noOfRecords = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0;
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer)m.get(RETURN_VALUE) : 0;
        Result<User> result = new Result(resultCode,list,noOfRecords,pageNumber,pageSize);
        return result;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> getFollowing(long userId, int pageNumber, int pageSize) {
        if (uspGetFollowing == null) {
            throw new IllegalStateException("uspGetFollowers cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id",userId)
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize);
        Map<String,Object> m = uspGetFollowing.execute(in);

        List<User> list = m.containsKey(LIST) ? (List<User>)m.get(LIST) : null;
        long noOfRecords = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0;
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer)m.get(RETURN_VALUE) : 0;
        Result<User> result = new Result(resultCode,list,noOfRecords,pageNumber,pageSize);
        return result;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> isFollower(long userId, long fanId) {
        if (uspIsFollower == null) {
            throw new IllegalStateException("uspIsFollower cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId",userId)
                .addValue("fanId",fanId);
        Map<String,Object> m = uspIsFollower.execute(in);

        List<User> list = m.containsKey(DATA) ? (List<User>)m.get(DATA) : null;
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer)m.get(RETURN_VALUE) : 0;
        return new Result(resultCode,list.get(0));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public Result<User> isFollowing(long userId, long fanId) {
        if (uspIsFollowing == null) {
            throw new IllegalStateException("uspIsFollowing cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId",userId)
                .addValue("fanId",fanId);
        Map<String,Object> m = uspIsFollowing.execute(in);

        List<User> list = m.containsKey(DATA) ? (List<User>)m.get(DATA) : null;
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer)m.get(RETURN_VALUE) : 0;
        return new Result(resultCode,list.get(0));
    }

    @Transactional
    @Override
    public Result<User> suspendUser(long userId, Date suspensionEndDate) {

        if (uspSuspendUser == null) {
            throw new IllegalStateException("uspSuspendUser cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId",userId)
                .addValue("suspensionEndDate",suspensionEndDate);
        Map<String,Object> m = uspSuspendUser.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<User>(resultCode);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Result<User> retrieveAllSuspendedUsers(int pageNumber, int pageSize) {

        if (uspRetrieveAllSuspendedUsers == null) {
            throw new IllegalStateException("uspSuspendUser cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize);
        Map<String,Object> m = uspRetrieveAllSuspendedUsers.execute(in);

        List<User> list = m.containsKey(LIST) ? (List<User>) m.get(LIST) : null;
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result(resultCode,list, !list.isEmpty() ? (long) list.size() : 0L,pageNumber,pageSize);
    }


    @Transactional
    public Result<User> unsuspendUser(long userId) {
        if (uspUnsuspendUser == null) {
            throw new IllegalStateException("uspSuspendUser cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("userId",userId);
        Map<String,Object> m = uspUnsuspendUser.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<User>(resultCode);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Result<User> retrieveAllLockedUsers(int pageNumber, int pageSize) {
        if (uspRetrieveAllLockedUsers == null) {
            throw new IllegalStateException("uspRetrieveAllLockedUsers");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize",pageSize);
        Map<String,Object> m = uspRetrieveAllLockedUsers.execute(in);

        List<User> list = m.containsKey(LIST) ? (List<User>) m.get(LIST) : null;
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<>(resultCode,list,!list.isEmpty() ? (long) list.size() : 0L, pageNumber, pageSize);
    }

    @Transactional
    public Result<User> unlockUserById(long id) {
        if (uspUnlockUserById == null) {
            throw new IllegalStateException("uspUnlockUserById cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("userId",id);
        Map<String,Object> m = uspUnlockUserById.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<User>(resultCode);
    }
}