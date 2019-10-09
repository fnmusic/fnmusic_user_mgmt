package com.fnmusic.user.management.messaging;

import com.fnmusic.base.messaging.impl.AbstractPublisher;
import com.fnmusic.base.models.SMS;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSPublisher extends AbstractPublisher<SMS> {

    @Autowired
    RabbitTemplate smsTemplate;

    @Override
    public void init() {
        rabbitTemplate = smsTemplate;
    }
}
