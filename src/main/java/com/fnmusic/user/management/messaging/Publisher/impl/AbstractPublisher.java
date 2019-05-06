package com.fnmusic.user.management.messaging.Publisher.impl;

import com.fnmusic.user.management.messaging.Publisher.IPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

@Component
public abstract class AbstractPublisher<T extends Object> implements IPublisher<T> {

    @Autowired
    protected RabbitTemplate rabbitTemplate;
    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    protected String routingKey;

    private static Logger logger = LoggerFactory.getLogger(AbstractPublisher.class);

    public abstract void init();

    @Override
    public void publishMessage(T message) {

        try {

            if (message == null) {
                throw new IllegalArgumentException("publish message cannot be null");
            }

            if (routingKey == null) {
                logger.error("Routing Key has not been initialized by using utilizing publisher");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte[] bytes = bos.toByteArray();

            rabbitTemplate.setExchange(exchange);
            rabbitTemplate.setRoutingKey(routingKey);
            rabbitTemplate.convertAndSend(bytes);

        } catch (Exception e) {
            logger.error("Unable to publish object");
        }
    }
}
