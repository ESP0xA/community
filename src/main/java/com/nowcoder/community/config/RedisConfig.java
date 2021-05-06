package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    // 定义第三方bean
    // 要把哪个对象装配到容器中就返回哪个对象
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {  // 将连接工厂注入给template以访问数据库；RedisConnectionFactory这个bean已经被容器装配，spring会自动将其注入给template

        // 实例化 template bean
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);     // 疑问：连接工厂是被注入给template这个bean还是redisTemplate方法？如果是注入给bean的话，此处为什么要设置给它？

        // 设置key序列化方式（数据转换方式，java to redis）
        template.setKeySerializer(RedisSerializer.string());    // RedisSerializer.string()返回一个能够序列化字符串的序列化器 RedisSerializer<String>
        // 设置value序列化方式
        template.setValueSerializer(RedisSerializer.json());    // value 会有各种数据结构，我们将其序列化为json
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setValueSerializer(RedisSerializer.json());    // value 会有各种数据结构，我们将其序列化为json
        // 设置完后触发生效
        template.afterPropertiesSet();

        return template;
    }
}
