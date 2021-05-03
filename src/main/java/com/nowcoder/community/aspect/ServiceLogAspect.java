package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

import java.util.Date;

// 统一记录日志
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 定义切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")    // 依次表示：第一个*表示返回值；其后的 com.nowcoder.community.service 表示包名；其后的 .*表示包下所有类；其后的 .* 表示所有方法；其后的 (..) 表示所有参数
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {   // 使用JoinPoint参数获取访问的类和方法
        // 日志格式：
        // 用户[1.2.3.4]，在[xxx]，访问了[com.nowcoder.community.service.xxx()].

        // 获取用户ip地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();   // ServletRequestAttributes 是 RequestAttributes的一个子类，强制转换成子类，功能更多
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();

        // 获取访问时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取用户访问的方法
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s]，在[%s], 访问了[%s].", ip, now, target));
    }

}
