package com.fnmusic.user.management.dao.impl;

import com.fnmusic.user.management.model.PasswordReset;
import com.fnmusic.user.management.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class AuthDaoImpl extends AbstractAuthDao<User> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall uspSubmitAccountActivationToken,uspSubmitPasswordResetToken, uspResetPassword;

    private static Logger logger = LoggerFactory.getLogger(AuthDaoImpl.class);

    @Autowired
    public void init() {
        this.uspCreateUser = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_create_user")
                .withReturnValue();

        this.uspSubmitAccountActivationToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_account_activation_token");

        this.uspSubmitPasswordResetToken = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_submit_password_reset_token")
                .withReturnValue();

        this.uspResetPassword = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_reset_password")
                .withReturnValue();
    }

    public void submitAccountActivationToken(String email, String accountActivationToken) {

        try {
            if (email.equals(null) || email.isEmpty()) {
                throw new IllegalArgumentException("email is invalid");
            }

            if (accountActivationToken.equals(null) || accountActivationToken.isEmpty()) {
                throw new IllegalArgumentException("account activation token is invalid");
            }

            if (uspSubmitAccountActivationToken.equals(null)) {
                throw new IllegalStateException("uspSubmitAccountActivationToken has not been initialized by utilizing dao");
            }

            SqlParameterSource in = new MapSqlParameterSource()
                    .addValue("email",email)
                    .addValue("token",accountActivationToken);

            uspSubmitAccountActivationToken.execute(in);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String retrieveAccountActivationToken(String email) {

        return email;
    }

    public void submitPasswordResetToken(String email, String passwordResetToken) {

        try {

            if (email.equals(null) || email.isEmpty()) {
                throw new IllegalArgumentException("email is invalid");
            }

            if (passwordResetToken.equals(null) || passwordResetToken.isEmpty()) {
                throw new IllegalArgumentException("account activation token is invalid");
            }

            if (uspSubmitPasswordResetToken == null) {
                throw new IllegalStateException("uspCreateUser has not been initialized by utilizing dao");
            }

            SqlParameterSource in = new MapSqlParameterSource()
                    .addValue("email",email)
                    .addValue("token",passwordResetToken);

            uspSubmitPasswordResetToken.execute(in);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void resetPassword(PasswordReset reset) {
    }



}
