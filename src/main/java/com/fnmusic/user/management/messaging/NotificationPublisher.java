package com.fnmusic.user.management.messaging;

import com.fnmusic.base.messaging.impl.AbstractPublisher;
import com.fnmusic.base.models.Notification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher extends AbstractPublisher<Notification> {

    @Autowired
    private RabbitTemplate notificationTemplate;

    @Override
    public void init() {
        this.abstractRabbitTemplate = notificationTemplate;
    }
}
