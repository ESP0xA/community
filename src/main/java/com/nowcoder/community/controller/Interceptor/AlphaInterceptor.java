package com.nowcoder.community.controller.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器测试
 */
@Component
public class AlphaInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    /**
     * 在Controller处理请求的代码之前执行的
     * @param request
     * @param response
     * @param handler   拦截目标
     * @return  请求是否被继续执行。如果返回false，则Controller不会继续往下执行。
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 看一下handler是个什么东西
        logger.debug("preHandle: " + handler.toString());
        return true;
    }


    /**
     * 在调用完Controller之后，模板引擎前执行
     * @param request
     * @param response
     * @param handler   拦截目标
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        logger.debug("postHandle: "  + handler.toString());
    }


    /**
     * 在模板引擎后最后执行
     * @param request
     * @param response
     * @param handler   拦截目标
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        logger.debug("afterCompletion: " + handler.toString());
    }
}
