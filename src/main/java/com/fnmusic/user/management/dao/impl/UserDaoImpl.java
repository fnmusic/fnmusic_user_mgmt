package com.fnmusic.user.management.dao.impl;

import com.fnmusic.user.management.dao.IUserDao;
import com.fnmusic.user.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements IUserDao<User> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall uspRetrieveUserByEmail,
            uspRetrieveUserByUsername,
            uspRetrieveUserById;

    protected final String IDENTITY_VALUE = "id";
    protected final String RETURN_VALUE = "RETURN_VALUE";
    protected final String NO_OF_RECORDS = "no_of_records";
    protected final String DATA = "data";
    protected final String LIST = "list";

    @Autowired
    private void init() {
        uspRetrieveUserByEmail = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("uspRetrieveUserByEmail")
                .withReturnValue()
                .returningResultSet(DATA, new BeanPropertyRowMapper<>(User.class));

        uspRetrieveUserByUsername = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("uspRetrieveUserByUsername")
                .withReturnValue()
                .returningResultSet(DATA, new BeanPropertyRowMapper<>(User.class));

        uspRetrieveUserById = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("uspRetrieveUserById")
                .withReturnValue()
                .returningResultSet(DATA,new BeanPropertyRowMapper<>(User.class));
    }

    @Transactional
    @Override
    public User retrieveUserByEmail(String email) {

        if (email == null)
            throw new IllegalArgumentException("email cannot be null");

        if (uspRetrieveUserByEmail == null)
            throw new IllegalStateException("uspRetrieve has not been initialized");

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        Map<String, Object> m = uspRetrieveUserByEmail.execute(in);
        List<User> list = null;
        if (m.containsKey(DATA)) {
            list = (List<User>) m.get(DATA);
        }

        if (list.isEmpty())
            return null;

        return list.get(0);
    }

    @Transactional
    @Override
    public User retrieveUserByUsername(String username) {

        if (username == null)
            throw new IllegalArgumentException(" username not be null");

        if (uspRetrieveUserByUsername == null)
            throw new IllegalStateException("uspRetrieve has not been initialized");

        SqlParameterSource in = new MapSqlParameterSource("username",username);
        Map<String, Object> m = uspRetrieveUserByUsername.execute(in);
        List<User> list = null;
        if (m.containsKey(DATA)) {
            list = (List<User>) m.get(DATA);
        }

        if (list.isEmpty())
            return null;

        return list.get(0);
    }

    @Transactional
    @Override
    public User retrieveUserById(Long id) {
        if (id == 0)
            throw new IllegalArgumentException("invalid user id");

        if (uspRetrieveUserById == null) {

        }

        return null;
    }
}
