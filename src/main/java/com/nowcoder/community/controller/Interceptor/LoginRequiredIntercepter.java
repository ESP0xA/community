package com.nowcoder.community.controller.Interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

// 该拦截器用于拦截非 @LoginRequied 标识的方法
@Component
public class LoginRequiredIntercepter implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    // 在请求前判断是否可以访问该方法

    /**
     * @param handler   拦截的目标
     * @return
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // 先判断拦截的目标是否是方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;                                                      // 便于获取信息
            Method method = handlerMethod.getMethod();                                                                  // 获取拦截到的Method对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);                                    // 获取注解
            // loginRequired != null 说明该方法是需要登录才可以访问的
            // hostHolder.getUser() == null 说明用户未登录
            if (loginRequired != null && hostHolder.getUser() == null) {                                                // 用户未登录时，访问注解标注的方法会被拒绝
                // 该方法是接口声明的，不能像Controller那样用模板的形式重定向
                // Controller重定向的底层也是这样实现的
                response.sendRedirect(request.getContextPath() + "/login");
                return false;                                                                                           // 拒绝请求
            }
        }

        return true;
    }
}
