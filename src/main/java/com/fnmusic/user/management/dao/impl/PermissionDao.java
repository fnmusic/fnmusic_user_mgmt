package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Permission;
import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.dao.IPermissionDao;
import com.fnmusic.user.management.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionDao extends AbstractBaseDao<Permission> implements IPermissionDao<Permission> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void init() {

        pspCreate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_create_permission")
                .withReturnValue();

        pspRetrieveByUniqueId = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_permission_by_id")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Permission.class));

        pspRetrieveByUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_permission_by_name")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(PermissionService.class));

        pspRetrieveAllByUniqueParameter = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_permission_by_feature")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Permission.class));

        pspRetrieveAll = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_all_permissions")
                .withReturnValue();

        pspUpdate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_update_permission")
                .withReturnValue();

        pspDelete = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_delete_permission")
                .withReturnValue();
    }

    @Override
    public Result<Permission> retrieveById(int id) {
        return retrieveByUniqueId(id);
    }

    @Override
    public Result<Permission> retrieveByName(String name) {
        return retrieveByUniqueKey(name);
    }

    @Override
    public Result<Permission> retrieveByFeature(String feature, int pageNumber, int pageSize) {
        return retrieveAllByUniqueParameter(feature, pageNumber, pageSize);
    }

    @Override
    public Result<Permission> delete(int id) {
        return deleteByUniqueId(id);
    }
}
