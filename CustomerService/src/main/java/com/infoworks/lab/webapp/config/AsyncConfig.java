package com.infoworks.lab.webapp.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private String appName;
    private int asyncCorePoolSize;

    public AsyncConfig(
            @Value("${spring.application.name}") String appName
            , @Value("${app.async.core.pool.size}") int asyncCorePoolSize) {
        this.appName = appName;
        this.asyncCorePoolSize = asyncCorePoolSize;
    }

    @Bean("SequentialExecutor")
    public Executor getSequentialExecutor() {
        Executor executor = Executors.newSingleThreadExecutor();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncCorePoolSize);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() / 2);
        executor.setThreadNamePrefix(appName + "-async-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, args) -> {
            System.out.println("Exception message - " + throwable.getMessage());
            System.out.println("Method name - " + method.getName());
            for (Object param : args) {
                System.out.println("Parameter value - " + param);
            }
        };
    }
}
