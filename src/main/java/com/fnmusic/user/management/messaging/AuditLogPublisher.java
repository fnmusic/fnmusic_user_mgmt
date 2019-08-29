package com.fnmusic.user.management.messaging;

import com.fnmusic.base.messaging.impl.AbstractPublisher;
import com.fnmusic.base.models.AuditLog;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditLogPublisher extends AbstractPublisher<AuditLog> {

    @Autowired
    private RabbitTemplate auditTemplate;

    @Override
    public void init() {
        this.abstractRabbitTemplate = auditTemplate;
    }
}
