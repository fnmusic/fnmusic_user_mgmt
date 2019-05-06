package com.fnmusic.user.management.messaging.Publisher.impl;

import com.fnmusic.user.management.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuditLogPublisher extends AbstractPublisher<AuditLog> {

    @Value("${app.rabbitmq.auditroutingkey}")
    private String auditRoutingKey;

    @Autowired
    @Override
    public void init() {
        this.routingKey = this.auditRoutingKey;
    }
}
