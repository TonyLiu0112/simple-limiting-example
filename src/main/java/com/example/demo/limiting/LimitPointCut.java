package com.example.demo.limiting;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@Aspect
public class LimitPointCut {

    private final RedisTemplate<String, String> redisTemplate;

    public LimitPointCut(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Around("@annotation(Limiting)")
    public Object limit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Limiting annotation = signature.getMethod().getAnnotation(Limiting.class);
        String key = annotation.value();
        if (acquire(key)) {
            return joinPoint.proceed();
        } else {
            return "limiting";
        }
    }

    private boolean acquire(String key) {
        String pop = redisTemplate.opsForSet().pop("bucket:" + key);
        return pop != null && pop.trim().length() > 0;
    }

}
