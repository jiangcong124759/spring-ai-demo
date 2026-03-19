//package com.example.demo.config;
//
//import com.alibaba.cloud.ai.memory.redis.BaseRedisChatMemoryRepository;
//import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AppConfig {
//
//    @Bean
//    public BaseRedisChatMemoryRepository redisChatMemory() {
//        return new RedissonRedisChatMemoryRepository.RedissonBuilder()
//                .host("127.0.0.1")
//                .port(6379)
//                .database(0)
//                .build();
//    }
//}