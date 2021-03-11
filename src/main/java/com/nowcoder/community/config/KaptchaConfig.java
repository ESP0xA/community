package com.nowcoder.community.config;

import java.util.Properties;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// spring没有对kaptcha进行自动配置，所以我们要自己写一个配置类，加载到Spring容器里
@Configuration
public class KaptchaConfig {

    // 交由IOC容器管理bean
    @Bean
    // 实例化Producer
    public Producer kaptchaProducer() {
        // DefaultKapcha是kaptcha的默认实现类
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        // 传入一些配置
        Properties properties = new Properties();   // key - value
        properties.setProperty("kaptcha.image.width", "100");   // 图片宽度
        properties.setProperty("kaptcha.image.height", "40");       // 图片高度
        properties.setProperty("kaptcha.textproducer.font.size", "32"); // 字号大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0"); // 字体颜色
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"); // 随机字符范围
        properties.setProperty("kaptcha.textproducer.char.length", "4"); // 字符长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");   // 选择噪声类

        Config config = new Config(properties);   // 传入properties对象
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
