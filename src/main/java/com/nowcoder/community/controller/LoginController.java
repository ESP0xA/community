package com.nowcoder.community.controller;

import java.util.*;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    // 将要调用userService的register方法
    @Autowired
    private UserService userService;

    /**
     *
     * @param model 将user对象通过model传给view
     * @param user 将页面注册时提交的账号、密码、邮箱等数据封装在user对象中
     * @return 视图
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        // 只要页面传入的值和user的属性相匹配，Spring MVC就会自动的将值注入给user
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            // 注册成功，即信息提交成功，等待用户激活，此时我们可以将页面设置一个跳转，跳转到operate result页面
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            // 注册失败，
            // 可能是一下三种原因之一，如果是，则参数非空
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }
}
