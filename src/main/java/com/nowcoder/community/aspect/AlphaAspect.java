package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

// 面向切面AOP示例
//@Component
//@Aspect
public class AlphaAspect {

    // 定义切点joinPoint
    // 通过表达式描述哪些bean和哪些方法属于处理的目标
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")    // 依次表示：第一个*表示返回值；其后的 com.nowcoder.community.service 表示包名；其后的 .*表示包下所有类；其后的 .* 表示所有方法；其后的 (..) 表示所有参数
    public void pointcut() {
    }

    // 定义通知

    // 在连接点开始的时候处理逻辑
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    // 在连接点结束的时候处理逻辑
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    // 在返回值以后处理逻辑
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("after returning");
    }

    // 在抛出异常时处理逻辑
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("after throwing");
    }

    // 在连接点前后都处理逻辑
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {   // 带一个连接点参数，表示织入的部位
        System.out.println("around before: do something...");
        Object obj = joinPoint.proceed();    // 调用目标对象被处理的方法（目标组件的方法）
        System.out.println("around after: do something...");
        return obj;
    }

}
