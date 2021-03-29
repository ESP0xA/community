package com.nowcoder.community.config;

import com.nowcoder.community.controller.Interceptor.AlphaInterceptor;
import com.nowcoder.community.controller.Interceptor.LoginRequiredIntercepter;
import com.nowcoder.community.controller.Interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器和一般的配置类不同，一般的配置类为的是配置一个第三方的Bean。而这里需要实现一个接口。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 配置拦截器，需要把拦截器注入进来
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredIntercepter loginRequiredIntercepter;

    /**
     *
     * @param registry  Spring在调用的时候会把该对象传进来，我们利用该对象注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 拦截器会拦截一切请求
        // registry.addInterceptor(alphaInterceptor);

        // 拦截器只拦截符合如下规则的请求
        registry.addInterceptor(alphaInterceptor)
                // /** 表示static目录下所有文件夹，这个会匹配static目录下所有的css、js和图片文件
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg")
                // 拦截器拦截指定路径的请求
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor)
                // /** 表示static目录下所有文件夹，这个会匹配static目录下所有的css、js和图片文件
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");
                // 拦截器拦截所有路径的请求

        registry.addInterceptor(loginRequiredIntercepter)
                // /** 表示static目录下所有文件夹，这个会匹配static目录下所有的css、js和图片文件
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");
                // 拦截器拦截所有路径的请求
    }
}
