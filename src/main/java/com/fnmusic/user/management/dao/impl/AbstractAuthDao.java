package com.fnmusic.user.management.dao.impl;

import com.fnmusic.user.management.dao.IAuthDao;
import com.fnmusic.user.management.model.Signup;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public abstract class AbstractAuthDao<T extends Object> implements IAuthDao<T> {


    protected SimpleJdbcCall uspCreateUser, uspIncreaseAccessFailedCount;

    protected final String IDENTITY_VALUE = "id";
    protected final String RETURN_VALUE = "RETURN_VALUE";
    protected final String NO_OF_RECORDS = "no_of_records";
    protected final String DATA = "data";
    protected final String LIST = "list";

    public abstract void init();

    @Transactional
    @Override
    public Long create(Signup model) {

        if (model == null)
            throw new IllegalArgumentException("Signup Object is null");

        if (uspCreateUser == null)
            throw new IllegalStateException("usp_create has not been initialized by utilizing dao");

        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
        Map<String,Object> m = uspCreateUser.execute(in);
        int resultCode = -1;
        if (m.containsKey(RETURN_VALUE)) {
            resultCode = (Integer) m.get(RETURN_VALUE);
        }

        Long id = null;

        if (m.containsKey(IDENTITY_VALUE)) {
            id = (Long) m.get(IDENTITY_VALUE);
        }

        return id;
    }

    @Transactional
    @Override
    public void increaseAccessFailedCount(String email) {

        if (uspIncreaseAccessFailedCount == null) {
            throw new IllegalStateException("uspIncreaseAccessFailedCount has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        uspIncreaseAccessFailedCount.execute(in);
    }
}
