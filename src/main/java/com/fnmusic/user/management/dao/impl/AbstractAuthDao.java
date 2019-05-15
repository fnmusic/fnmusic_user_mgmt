package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.models.Result;
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
    public Result<T> createUser(Signup model) {

        if (model == null) {
            throw new IllegalArgumentException("Signup Object is null");
        }

        if (uspCreateUser == null) {
            throw new IllegalStateException("usp_create has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new BeanPropertySqlParameterSource(model);
        Map<String,Object> m = uspCreateUser.execute(in);
        int resultCode = m.containsValue(RETURN_VALUE) ? (Integer) m.get(RETURN_VALUE) : 0;
        Long id = m.containsKey(IDENTITY_VALUE) ? (Long) m.get(IDENTITY_VALUE) : 0;

        Result result = new Result(resultCode,id);
        return result;
    }

    @Transactional
    @Override
    public void increaseUserAccessFailedCount(String email) {

        if (uspIncreaseAccessFailedCount == null) {
            throw new IllegalStateException("uspIncreaseAccessFailedCount has not been initialized by utilizing dao");
        }

        SqlParameterSource in = new MapSqlParameterSource("email",email);
        uspIncreaseAccessFailedCount.execute(in);
    }

}
