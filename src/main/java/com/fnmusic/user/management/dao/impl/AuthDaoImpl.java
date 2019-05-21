package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.dao.IAuthDao;
import com.fnmusic.user.management.models.UserAuth;
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
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Repository
public class AuthDaoImpl extends AbstractBaseDao<UserAuth> implements IAuthDao<UserAuth> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall uspIncreaseLoginAttempt,
            uspSubmitActivationToken,
            uspRetrieveActivationToken,
            uspActivateAccount,
            uspSubmitPasswordResetToken,
            uspRetrievePasswordResetToken,
            uspResetPassword;

    private final String STATUS = "status";
    private static Logger logger = LoggerFactory.getLogger(AuthDaoImpl.class);

    @Override
    public void init() {
        uspIncreaseLoginAttempt = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_increase_access_failed_count").withReturnValue();
        uspSubmitActivationToken = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_Submit_Activation_Token").withReturnValue();
        uspRetrieveActivationToken = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_retrieve_activation_token").returningResultSet(DATA, BeanPropertyRowMapper.newInstance(String.class));
        uspActivateAccount = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_activate_account").withReturnValue();
        uspSubmitPasswordResetToken = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_submit_password_reset_token").withReturnValue();
        uspRetrievePasswordResetToken = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_retrieve_password_reset_token").returningResultSet(DATA, BeanPropertyRowMapper.newInstance(String.class));
        uspResetPassword = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_reset_password").withReturnValue();
    }

    @Transactional
    @Override
    public Result<UserAuth> increaseLoginAttempt(@NotNull @NotEmpty String email) {

        if (uspIncreaseLoginAttempt == null) {
            throw new IllegalStateException("uspIncreaseAccessFailedCount has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new MapSqlParameterSource("email", email);
        Map<String,Object> m = uspIncreaseLoginAttempt.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        Result<UserAuth> result = null;
        if (m.containsKey(STATUS)) {
            boolean status = (boolean) m.get(STATUS);
            return result = new Result<UserAuth>(resultCode,status);
        }

        return result;
    }

    @Validated
    @Transactional
    @Override
    public Result<UserAuth> submitActivationToken(@NotNull UserAuth model) {

        if (uspSubmitActivationToken.equals(null)) {
            throw new IllegalStateException("usp_Submit_Activation_Token has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
        Map<String,Object> m = uspSubmitActivationToken.execute(in);
        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        long id = m.containsKey(IDENTITY) ? (long) m.get(IDENTITY) : 0L;
        Result<UserAuth> result = new Result<>();
        result.setData(model);
        result.setResultCode(resultCode);
        result.setIdentityValue(id);
        result.setStatus(m.containsKey(STATUS) && (boolean) m.get(STATUS));

        return result;

    }
//
//    @Transactional
//    @SuppressWarnings("unchecked")
//    @Override
//    public Result<UserAuth> retrieveActivationToken(@NotNull @NotEmpty String email) {
//
//        if (uspRetrieveActivationToken == null) {
//            throw new IllegalStateException("uspRetrievePasswordResetToken cannot ne null");
//        }
//
//        SqlParameterSource in = new MapSqlParameterSource("email",email);
//        Map<String,Object> m = uspRetrieveActivationToken.execute(in);
//        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
//        List<UserAuth> list = m.containsKey(DATA) ? (List<UserAuth>) m.get(DATA) : null;
//        Result result = null;
//        if (!list.isEmpty() || list != null) {
//            result = new Result<>(resultCode,list.get(0));
//        }
//
//        return result;
//    }
//
//    @Transactional
//    @Override
//    public Result<UserAuth> activateAccount(@NotNull UserAuth model) {
//
//        if (uspActivateAccount == null) {
//            throw new IllegalStateException("uspActivateAccount cannot be null");
//        }
//
//        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
//        Map<String,Object> m = uspActivateAccount.execute(in);
//        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
//        if (m.containsKey(STATUS)) {
//            Result<UserAuth> result = new Result<UserAuth>(resultCode,(boolean) m.get(STATUS));
//            return result;
//        }
//
//        return null;
//    }
//
//    @Transactional
//    @Override
//    public Result<UserAuth> submitPasswordResetToken(@NotNull UserAuth model) {
//
//        if (uspSubmitPasswordResetToken == null) {
//            throw new IllegalStateException("uspCreateUser has not been initialized by utilizing dao");
//        }
//
//        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
//        Map<String,Object> m = uspSubmitPasswordResetToken.execute(in);
//        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
//        if (m.containsKey(STATUS)) {
//            Result<UserAuth> result = new Result<>(resultCode,model,(boolean) m.get(STATUS));
//            return result;
//        }
//
//        return null;
//    }
//
//    @Transactional
//    @Validated
//    @SuppressWarnings("unchecked")
//    @Override
//    public Result<UserAuth> retrievePasswordResetToken(@NotEmpty @NotNull String email) {
//
//        if (uspRetrievePasswordResetToken == null) {
//            throw new IllegalStateException("uspRetrievePasswordResetToken cannot ne null");
//        }
//
//        SqlParameterSource in = new MapSqlParameterSource("email",email);
//        Map<String,Object> m = uspRetrieveActivationToken.execute(in);
//        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
//        List<UserAuth> list = m.containsKey(DATA) ? (List<UserAuth>) m.get(DATA) : null;
//        Result result = null;
//        if (!list.isEmpty() || list != null) {
//            result = new Result<>(resultCode,list.get(0));
//        }
//
//        return result;
//    }
//
//
//
//    @Transactional
//    @Override
//    public Result<UserAuth> resetPassword(UserAuth model) {
//
//        if (uspResetPassword == null) {
//            throw new IllegalStateException("uspResetPassword cannot be null");
//        }
//
//        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
//        Map<String, Object> m = uspResetPassword.execute(in);
//        int resultCode = m.containsKey(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
//        if (m.containsKey(STATUS)) {
//            Result<UserAuth> result = new Result<UserAuth>(resultCode,(boolean) m.get(STATUS));
//            return result;
//        }
//
//        return null;
//    }


}
