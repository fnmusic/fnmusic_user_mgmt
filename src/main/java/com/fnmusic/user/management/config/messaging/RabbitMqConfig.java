package com.fnmusic.user.management.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by Stephen.Enunwah on 4/6/2019
 */

@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.auditroutingkey}")
    private String auditRoutingKey;
    @Value("${app.rabbitmq.auditqueue}")
    private String auditQueue;
    @Value("${app.rabbitmq.mailroutingkey}")
    private String mailRoutingKey;
    @Value("${app.rabbitmq.mailqueue}")
    private String mailQueue;

    @Bean
    TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    Queue auditQueue() {
        return new Queue(auditQueue, true);
    }

    @Bean
    Queue mailQueue() {
        return new Queue(mailQueue,true);
    }

    @Bean
    Binding auditbinding(Queue auditQueue, TopicExchange exchange){
        return BindingBuilder
                .bind(auditQueue)
                .to(exchange)
                .with(auditRoutingKey);
    }

    @Bean
    Binding mailBinding(Queue mailQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(mailQueue)
                .to(exchange())
                .with(mailRoutingKey);
    }

}
