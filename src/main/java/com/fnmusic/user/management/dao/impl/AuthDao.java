package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.dao.IAuthDao;
import com.fnmusic.user.management.models.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class AuthDao extends AbstractBaseDao<Auth> implements IAuthDao<Auth> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall uspSubmitLoginVerificationToken,
            uspRetrieveLoginVerificationToken,
            uspRetrieveAllLoginVerificationTokens,
            uspDeleteLoginVerificationToken,
            uspSubmitActivationToken,
            uspRetrieveActivationToken,
            uspRetrieveAllActivationTokens,
            uspDeleteActivationToken,
            uspActivateAccount,
            uspSubmitForgotPasswordVerificationToken,
            uspRetrieveForgotPasswordVerificationToken,
            uspRetrieveAllForgotPasswordVerificationTokens,
            uspDeleteForgotPasswordVerificationToken,
            uspSubmitPasswordResetToken,
            uspRetrievePasswordResetToken,
            uspRetrieveAllPasswordResetTokens,
            uspDeletePasswordResetToken,
            uspResetPassword;

    private static Logger logger = LoggerFactory.getLogger(AuthDao.class);

    @Override
    public void init() {

        uspSubmitLoginVerificationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_login_verification_token")
                .withReturnValue();

        uspRetrieveLoginVerificationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_login_verification_token")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Auth.class));

        uspRetrieveAllLoginVerificationTokens = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_login_verification_tokens")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Auth.class));

        uspDeleteLoginVerificationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_delete_login_verification_token")
                .withReturnValue();

        uspSubmitActivationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_activation_token")
                .withReturnValue();

        uspRetrieveActivationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_activation_token")
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Auth.class));

        uspRetrieveAllActivationTokens = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_activation_tokens")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Auth.class));

        uspDeleteActivationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_delete_activation_token")
                .withReturnValue();

        uspActivateAccount = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_activate_account")
                .withReturnValue();

        uspSubmitForgotPasswordVerificationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_forgot_password_verification_token")
                .withReturnValue();

        uspRetrieveForgotPasswordVerificationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_forgot_password_verification_token")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Auth.class));

        uspRetrieveAllForgotPasswordVerificationTokens = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_forgot_password_verification_tokens")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Auth.class));

        uspDeleteForgotPasswordVerificationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_delete_forgot_password_verification_token")
                .withReturnValue();

        uspSubmitPasswordResetToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_password_reset_token")
                .withReturnValue();

        uspRetrievePasswordResetToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_password_reset_token")
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Auth.class));

        uspRetrieveAllPasswordResetTokens = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_retrieve_all_password_reset_tokens")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Auth.class));

        uspDeletePasswordResetToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_delete_password_reset_token")
                .withReturnValue();

        uspResetPassword = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_reset_password")
                .withReturnValue();
    }

    @Transactional
    @Override
    public Result<Auth> submitLoginVerificationToken(Auth auth) {

        if (uspSubmitLoginVerificationToken == null) {
            throw new IllegalStateException("uspSubmitLoginVerificationToken cannot be null");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(auth);
        Map<String,Object> m = uspSubmitLoginVerificationToken  .execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        long id = m.containsKey(IDENTITY) && m.get(IDENTITY) != null ? (Long) m.get(IDENTITY) : 0L;
        return new Result<>(resultCode,id);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveLoginVerificationToken(String email) {

        if (uspRetrieveLoginVerificationToken == null) {
            throw new IllegalStateException("uspRetrieveLoginVerificationToken cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        Map<String,Object> m = uspRetrieveLoginVerificationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(DATA) ? (List<Auth>) m.get(DATA) : null;
        return new Result<>(resultCode, list.get(0));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveAllLoginVerificationTokens(int pageNumber, int pageSize) {

        if (uspRetrieveAllLoginVerificationTokens == null) {
            throw new IllegalStateException("uspRetrieveAllLoginVerificationTokens cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize)
                .addValue("no_of_records",null);
        Map<String,Object> m = uspRetrieveAllLoginVerificationTokens.execute(in);

        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(LIST) ? (List<Auth>) m.get(LIST) : null;
        long no_of_records = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0L;
        return new Result(resultCode,list,no_of_records,pageNumber,pageSize);
    }

    @Override
    public Result<Auth> deleteLoginVerificationToken(String email) {

        if (uspDeleteLoginVerificationToken == null) {
            throw new IllegalStateException("uspDeleteLoginVerificationToken cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email", email);
        Map<String, Object> m = uspDeleteLoginVerificationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<Auth>(resultCode);
    }

    @Transactional
    @Override
    public Result<Auth> submitActivationToken(Auth auth) {

        if (uspSubmitActivationToken == null) {
            throw new IllegalStateException("usp_Submit_Activation_Token has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(auth);
        Map<String,Object> m = uspSubmitActivationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        long id = m.containsKey(IDENTITY) && m.get(IDENTITY) != null ? (Long) m.get(IDENTITY) : 0L;
        return new Result<>(resultCode,id);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveActivationToken(String email) {

        if (uspRetrieveActivationToken == null) {
            throw new IllegalStateException("uspRetrievePasswordResetToken cannot ne null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        Map<String,Object> m = uspRetrieveActivationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(DATA) ? (List<Auth>) m.get(DATA) : null;
        return new Result<>(resultCode, list.get(0));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveAllActivationTokens(int pageNumber, int pageSize) {

        if (uspRetrieveAllActivationTokens == null) {
            throw new IllegalStateException("uspRetrieveAllActivationTokens cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize)
                .addValue("no_of_records",null);
        Map<String,Object> m = uspRetrieveAllActivationTokens.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(LIST) ? (List<Auth>) m.get(LIST) : null;
        long no_of_records = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0L;
        return new Result<>(resultCode,list,no_of_records,pageNumber,pageSize);
    }

    @Override
    public Result<Auth> deleteActivationToken(String email) {

        if (uspDeleteActivationToken == null) {
            throw new IllegalStateException("uspDeleteActivationToken cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email", email);
        Map<String, Object> m = uspDeleteActivationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<Auth>(resultCode);
    }

    @Transactional
    @Override
    public Result<Auth> activateAccount(Auth model) {

        if (uspActivateAccount == null) {
            throw new IllegalStateException("uspActivateAccount cannot be null");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
        Map<String,Object> m = uspActivateAccount.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<Auth>(resultCode);
    }

    @Transactional
    @Override
    public Result<Auth> submitForgotPasswordVerificationToken(Auth auth) {
        if (uspSubmitForgotPasswordVerificationToken == null) {
            throw new IllegalStateException("uspSubmitForgotPasswordVerificationToken cannot be null");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(auth);
        Map<String,Object> m = uspSubmitForgotPasswordVerificationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        long id = m.containsKey(IDENTITY) && m.get(IDENTITY) != null ? (Long) m.get(IDENTITY) : 0L;
        return new Result<>(0,id);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveForgotPasswordVerificationToken(String email) {
        if (uspRetrieveForgotPasswordVerificationToken == null) {
            throw new IllegalStateException("uspRetrieveForgotPasswordVerificationToken cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        Map<String,Object> m = uspRetrieveForgotPasswordVerificationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(DATA) ? (List<Auth>) m.get(DATA) : null;
        return new Result<>(resultCode,!list.isEmpty() ? list.get(0) : null);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveAllForgotPasswordVerificationTokens(int pageNumber, int pageSize) {

        if (uspRetrieveAllForgotPasswordVerificationTokens == null) {
            throw new IllegalStateException("uspRetrieveAllActivationTokens cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize)
                .addValue("no_of_records",null);
        Map<String,Object> m = uspRetrieveAllForgotPasswordVerificationTokens.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(LIST) ? (List<Auth>) m.get(LIST) : null;
        long no_of_records = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0L;
        return new Result<>(resultCode,list,no_of_records,pageNumber,pageSize);
    }

    @Override
    public Result<Auth> deleteForgotPasswordVerificationToken(String email) {

        if (uspDeleteForgotPasswordVerificationToken == null) {
            throw new IllegalStateException("uspDeleteForgotPasswordVerificationToken cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email", email);
        Map<String, Object> m = uspDeleteForgotPasswordVerificationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<Auth>(resultCode);
    }

    @Transactional
    @Override
    public Result<Auth> submitPasswordResetToken(Auth model) {

        if (uspSubmitPasswordResetToken == null) {
            throw new IllegalStateException("uspCreateUser has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
        Map<String,Object> m = uspSubmitPasswordResetToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        long id = m.containsKey(IDENTITY) && m.get(IDENTITY) != null ? (Long) m.get(IDENTITY) : 0L;
        return new Result<Auth>(resultCode,id);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrievePasswordResetToken(String email) {

        if (uspRetrievePasswordResetToken == null) {
            throw new IllegalStateException("uspRetrievePasswordResetToken cannot ne null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        Map<String,Object> m = uspRetrievePasswordResetToken.execute(in);
        
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(DATA) ? (List<Auth>) m.get(DATA) : null;
        return new Result<>(resultCode, list.get(0));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Result<Auth> retrieveAllPasswordResetTokens(int pageNumber, int pageSize) {

        if (uspRetrieveAllPasswordResetTokens == null) {
            throw new IllegalStateException("uspRetrieveAllActivationTokens cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber",pageNumber)
                .addValue("pageSize",pageSize)
                .addValue("no_of_records",null);
        Map<String,Object> m = uspRetrieveAllPasswordResetTokens.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        List<Auth> list = m.containsKey(LIST) ? (List<Auth>) m.get(LIST) : null;
        long no_of_records = m.containsKey(NO_OF_RECORDS) ? (Long) m.get(NO_OF_RECORDS) : 0L;
        return new Result<>(resultCode,list,no_of_records,pageNumber,pageSize);
    }

    @Override
    public Result<Auth> deletePasswordResetToken(String email) {

        if (uspDeletePasswordResetToken == null) {
            throw new IllegalStateException("uspDeleteLoginVerificationToken cannot be null");
        }

        SqlParameterSource in = new MapSqlParameterSource("email", email);
        Map<String, Object> m = uspDeletePasswordResetToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<Auth>(resultCode);
    }

    @Transactional
    @Override
    public Result<Auth> resetPassword(Auth auth) {

        if (uspResetPassword == null) {
            throw new IllegalStateException("uspResetPassword cannot be null");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(auth);
        Map<String, Object> m = uspResetPassword.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        return new Result<Auth>(resultCode);
    }


}
