package com.fnmusic.user.management.messaging;

import com.fnmusic.base.messaging.impl.AbstractPublisher;
import com.fnmusic.base.models.Mail;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailPublisher extends AbstractPublisher<Mail> {

    @Autowired
    private RabbitTemplate mailTemplate;

    @Override
    public void init() {
        this.abstractRabbitTemplate = mailTemplate;
    }
}
