package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;
import com.fnmusic.user.management.dao.IRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDao extends AbstractBaseDao<Role> implements IRoleDao<Role> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void init() {

        pspCreate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_create_role")
                .withReturnValue();

        pspRetrieveByUniqueId = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_role_by_id")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Role.class));

        pspRetrieveByUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_role_by_name")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Role.class));

        pspRetrieveAll = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_all_roles")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Role.class));

        pspUpdate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_update_role")
                .withReturnValue();

        pspDelete = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_delete_role_by_id")
                .withReturnValue();


    }

    @Override
    public Result<Role> retrieveById(int id) {
        return retrieveByUniqueId(id);
    }

    @Override
    public Result<Role> retrieveByName(String name) {
        return retrieveByUniqueKey(name);
    }

    @Override
    public Result<Role> deleteByUniqueId(int id) {
        return deleteByUniqueId(id);
    }
}
