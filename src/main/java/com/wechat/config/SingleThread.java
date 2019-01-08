package com.wechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SingleThread {
    @Bean
    public ExecutorService executorService() {
        return Executors.newSingleThreadExecutor();
    }
}
