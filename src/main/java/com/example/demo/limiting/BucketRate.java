package com.example.demo.limiting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class BucketRate {

    private final Logger logger = LoggerFactory.getLogger(BucketRate.class);
    private final RedisTemplate<String, String> redisTemplate;
    private ExecutorService executors = Executors.newSingleThreadExecutor();
    private final String key = "bucket:index";
    private final AtomicBoolean isLimiting = new AtomicBoolean(true);

    public BucketRate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        executors.submit(() -> {
            while (isLimiting.get()) {
                try {
                    Long size = redisTemplate.opsForSet().size(key);
                    if (size == null || size < 1)
                        redisTemplate.opsForSet().add(key, UUID.randomUUID().toString());
                    Thread.sleep(10000);
                    logger.info("put certificate into bucket.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
