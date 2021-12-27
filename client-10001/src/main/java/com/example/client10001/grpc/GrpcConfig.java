package com.example.client10001.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Configuration
public class GrpcConfig {

    @Bean(name = "grpcExecutorPool")
    public Executor grpcAsyncServiceExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(50);

        executor.setMaxPoolSize(100);

        executor.setQueueCapacity(200);

        executor.setThreadNamePrefix("grpc-service");

        executor.setRejectedExecutionHandler((r, executor1) -> {
            if (!executor1.isShutdown()) {
                executor1.getQueue().poll();
                executor1.execute(r);
            }
            log.warn("Task " + r.toString() +
                    " rejected from " +
                    executor1);
            throw new RejectedExecutionException();
        });

        return executor;
    }

}
