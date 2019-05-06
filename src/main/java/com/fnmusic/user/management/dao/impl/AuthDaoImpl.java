package com.fnmusic.user.management.dao.impl;

import com.fnmusic.user.management.model.PasswordReset;
import com.fnmusic.user.management.model.User;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Map;

@Repository
public class AuthDaoImpl extends AbstractAuthDao<User> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall uspSubmitPasswordResetToken, uspResetPassword;

    private static Logger logger = LoggerFactory.getLogger(AuthDaoImpl.class);

    @Autowired
    public void init() {
        this.uspCreateUser = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("uspCreateUser")
                .withReturnValue();

        this.uspSubmitPasswordResetToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_password_reset_token")
                .withReturnValue();

        this.uspResetPassword = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_reset_password")
                .withReturnValue();
    }

    public void submitPasswordResetToken(String email, String passwordResetToken) throws Exception {
        if (uspSubmitPasswordResetToken == null) {
            throw new IllegalStateException("uspCreateUser has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("email",email)
                .addValue("passwordResetToken",passwordResetToken);

        Map<String,Object> m = uspSubmitPasswordResetToken.execute(in);
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        if (resultCode > 0) {
            logger.error("Something went wrong while generating password reset link for " + email);
            throw new Exception("");
        }
    }

    public void resetPassword(PasswordReset reset) {
    }


}
