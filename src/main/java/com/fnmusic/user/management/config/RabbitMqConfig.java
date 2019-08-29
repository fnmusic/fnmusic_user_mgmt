package com.fnmusic.user.management.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by Stephen.Enunwah on 4/6/2019
 */

@Configuration
public class RabbitMQConfig {

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
    @Value("${app.rabbitmq.notificationroutingkey}")
    private String notificationRoutingKey;
    @Value("${app.rabbitmq.notificationqueue}")
    private String notificationQueue;

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue(auditQueue, true);
    }

    @Bean
    public Queue mailQueue() {
        return new Queue(mailQueue,true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue,true);
    }

    @Bean
    public Binding auditbinding(Queue auditQueue, TopicExchange exchange){
        return BindingBuilder.bind(auditQueue).to(exchange).with(auditRoutingKey);
    }

    @Bean
    public Binding mailBinding(Queue mailQueue, TopicExchange exchange) {
        return BindingBuilder.bind(mailQueue).to(exchange()).with(mailRoutingKey);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(notificationRoutingKey);
    }

    @Bean(name = "mailTemplate")
    public RabbitTemplate mailRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey(mailRoutingKey);

        return rabbitTemplate;
    }

    @Bean(name = "auditTemplate")
    public RabbitTemplate auditRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey(auditRoutingKey);

        return rabbitTemplate;
    }

    @Bean(name = "notificationTemplate")
    public RabbitTemplate notificationRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey(notificationRoutingKey);

        return rabbitTemplate;

    }

}
