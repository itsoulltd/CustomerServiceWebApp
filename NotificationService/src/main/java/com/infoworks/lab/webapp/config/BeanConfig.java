package com.infoworks.lab.webapp.config;

import com.infoworks.lab.cache.MemCache;
import com.infoworks.lab.datasources.RedisDataSource;
import com.infoworks.lab.datasources.RedissonDataSource;
import com.infoworks.lab.domain.models.Otp;
import com.it.soul.lab.data.base.DataSource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;

@Configuration
public class BeanConfig {

    private Environment env;

    public BeanConfig(Environment env) {
        this.env = env;
    }

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean
    RedissonClient getRedisClient(){
        String redisHost = env.getProperty("app.redis.host") != null
                ? env.getProperty("app.redis.host") : "localhost";
        String redisPort = env.getProperty("app.redis.port") != null
                ? env.getProperty("app.redis.port") : "6379";
        Config conf = new Config();
        conf.useSingleServer()
                .setAddress(String.format("redis://%s:%s",redisHost, redisPort))
                .setRetryAttempts(5)
                .setRetryInterval(1500);
        //Redisson-Client instance are fully-thread safe.
        return Redisson.create(conf);
    }

    @Bean("otpCache")
    DataSource<String, Otp> getOtpCache(RedissonClient client) {
        long ttl = Long.valueOf(env.getProperty("app.otp.ttl.minute"));
        RedisDataSource dataSource = new RedissonDataSource(client, Duration.ofMinutes(ttl).toMillis());
        return new MemCache<>(dataSource, Otp.class);
    }

}
