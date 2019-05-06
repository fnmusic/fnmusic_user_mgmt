package com.fnmusic.user.management.messaging.Publisher;

import org.springframework.stereotype.Service;

@Service
public interface IPublisher<T> {

    public void publishMessage(T message);
}
