package com.fnmusic.user.management.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String auditLogId;
    private String action;
    private String detail;
    private Date createdDate;
    private String entityId;
    private String entityName;
    private Object auditObject;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = UUID.randomUUID().toString();
    }

    public String getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(String auditLogId) {
        this.auditLogId = String.valueOf(new Random().nextInt());
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Object getAuditObject() {
        return auditObject;
    }

    public void setAuditObject(Object auditObject) {
        this.auditObject = auditObject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
