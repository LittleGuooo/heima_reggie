package com.xu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

@SpringBootTest
public class TestRedis {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void Test1(){
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("name","xu");

        Set keys = redisTemplate.keys("name");
        System.out.println(keys);


    }
}
