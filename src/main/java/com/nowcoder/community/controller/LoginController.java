package com.nowcoder.community.controller;

import java.util.*;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController implements CommunityConstant {

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

    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int res = userService.activation(userId, code);
        if (res == ACTIVATION_SUCCESS) {    // 激活成功，跳转到登陆页面
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (res == ACTIVATION_REPEAT){   // 重复激活，跳转到首页
            model.addAttribute("msg", "无效操作，您的账号已经激活过了！");
            model.addAttribute("target", "/index");
        } else {                            // 激活失败，跳转到首页
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";  // operate-result页面会根据传入的msg参数动态显示结果，根据target参数做出跳转
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

}
