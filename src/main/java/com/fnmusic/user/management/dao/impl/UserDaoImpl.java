package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.user.management.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDaoImpl extends AbstractBaseDao<User> implements IUserDao<User> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void init() {
        pspCreate = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_create_user").withReturnValue();
        pspRetrieveByUniqueId = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_RetrieveUser_By_Id").withReturnValue().returningResultSet(DATA , BeanPropertyRowMapper.newInstance(User.class));
        pspRetrieveByUniqueKey = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_Retrieve_User_By_Email").withReturnValue().returningResultSet(DATA , BeanPropertyRowMapper.newInstance(User.class));
        pspRetrieveBySecondUniqueKey = new SimpleJdbcCall(jdbcTemplate).withSchemaName("dbo").withProcedureName("usp_Retrieve_User_By_Username").withReturnValue().returningResultSet(DATA, BeanPropertyRowMapper.newInstance(User.class));
    }

    @Transactional
    @Override
    public Result<User> create(User object) {
        return super.create(object);
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
    public Result<User> update(User object) {
        return super.update(object);
    }

    @Transactional
    @Override
    public Result<User> delete(String email) {
        return super.deleteByUniqueKey(email);
    }
}
