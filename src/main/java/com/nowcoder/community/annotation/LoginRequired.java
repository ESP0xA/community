package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解用于标注哪些方法可以被访问
@Target(ElementType.METHOD)                                                                                             // 元注解，声明作用域
@Retention(RetentionPolicy.RUNTIME)                                                                                     // 元注解，声明生效时间范围
public @interface LoginRequired {

}
