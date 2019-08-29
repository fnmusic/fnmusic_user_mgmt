package com.fnmusic.user.management.dao.impl;

import com.fnmusic.base.dao.AbstractBaseDao;
import com.fnmusic.base.models.Feature;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;
import com.fnmusic.user.management.dao.IFeatureDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class FeatureDao extends AbstractBaseDao<Feature> implements IFeatureDao<Feature> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall pspRetrieveByRole;

    @Override
    public void init() {

        pspCreate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_create_feature")
                .withReturnValue();

        pspRetrieveByUniqueId = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_feature_by_id")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Feature.class));

        pspRetrieveByUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_feature_by_name")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Feature.class));

        pspRetrieveBySecondUniqueKey = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_feature_by_role")
                .withReturnValue()
                .returningResultSet(DATA, BeanPropertyRowMapper.newInstance(Feature.class));

        pspRetrieveAll = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_retrieve_all_features")
                .withReturnValue()
                .returningResultSet(LIST, BeanPropertyRowMapper.newInstance(Feature.class));

        pspUpdate = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_update_feature")
                .withReturnValue();

        pspDelete = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("psp_delete_feature")
                .withReturnValue();
    }

    @Override
    public Result<Feature> retrieveById(int id) {
        return retrieveByUniqueId(id);
    }

    @Override
    public Result<Feature> retrieveByName(String name) {
        return retrieveByUniqueKey(name);
    }

    @Override
    public Result<Feature> retrieveByRole(String role) {
        return retrieveBySecondUniqueKey(role);
    }

    @Override
    public Result<Feature> delete(int id) {
        return deleteByUniqueId(id);
    }
}
