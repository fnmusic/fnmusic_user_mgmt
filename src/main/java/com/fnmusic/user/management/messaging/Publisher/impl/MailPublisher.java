package com.fnmusic.user.management.messaging.Publisher.impl;

import com.fnmusic.base.models.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailPublisher extends AbstractPublisher<Mail> {

    @Value("${app.rabbitmq.mailroutingkey}")
    private String mailRoutingKey;

    @Autowired
    @Override
    public void init() {
        this.routingKey = this.mailRoutingKey;
    }
}
