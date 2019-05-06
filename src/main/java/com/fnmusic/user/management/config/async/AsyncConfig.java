package com.fnmusic.user.management.config.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

//    @Bean("authExecutor")
//    public Executor authExecutor() {
//        return new ThreadPoolTaskExecutor();
//    }
//
//    @Override
//    public Executor getAsyncExecutor() {
//        return new ThreadPoolTaskExecutor();
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return null;
//    }
}
