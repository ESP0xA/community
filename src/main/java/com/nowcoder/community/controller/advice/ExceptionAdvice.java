package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 全局配置类，用于统一处理异常
// 如果不加限制，那么该配置类会扫描所有的bean
// 加上如下限制后，组件只会扫描带有 @Controller 注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    // 实例化日志组件
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    // 统一处理异常的方法
    // 指定处理哪些异常
    // 该方法可以带若干参数，但是我们一般只用三个
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {      // 参数分别是：Controller中发生的异常；用于处理响应的 request 和 response
        // 当执行到该方法时，说明已经发生异常。
        logger.error("服务器发生异常：" + e.getMessage());

        // 将异常的栈的信息记录下来
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 给浏览器一个响应，重定向到错误页面
        // 需要判断用户的请求是普通请求还是异步请求，如果是普通请求那么直接重定向就行了；如果是异步请求，那么返回一个json，重定向页面就失去意义了
        // 以下方法可以判断是否为异步请求:
        // 尝试从 header 中获取一个值
        String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 如果是异步请求就响应一个 String
            // application/plain说明这是一个普通的字符串，后面我们会手动转换为json
            httpServletResponse.setContentType("application/plain;charset=utf-8");
            // 声明一个输出流以输出字符串
            PrintWriter writer = httpServletResponse.getWriter();   // 处理异常，如果异常则抛出 (IOException)
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            // 否则是普通请求
            // 重定向到错误页面
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/error");
        }
    }
}
