package com.nowcoder.community.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController implements CommunityConstant {

    // 获取注册页面

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }


    // 注册账户

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


    // 激活账户

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


    // 获取登陆页面

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }


    // 获取验证码

    @Autowired
    private Producer kaptchaProducer;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    // 返回的一张图片，用response的方式直接放回，所以返回对象是void
    // 第一次请求登陆页面，会返回一张验证码图片，再次请求登陆的时候，会进行验证。这是一个跨请求的操作，而验证码数据属于敏感信息
    // 根据以上特征，我们选择使用session存储相关信息。
    public void getKaptcha(HttpServletResponse response, HttpSession session) {

        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session（存放在服务器内存中）
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        // 声明向浏览器返回的数据类型
        response.setContentType("image/png");
        // 获取response的输出流
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            // e.printStackTrace();
            logger.error("响应验证码失败：" + e.getMessage());
        }
        // response不用手动关闭，它是由SpringMVC去维护的
    }


    // 提交登录信息

    @Value("${server.servlet.context-path")
    private String contextPath;
    /**
     *
     * @param username      用户名
     * @param password      密码
     * @param code          验证码（存放在session里）
     * @param rememberMe    是否记住用户登录状态（用户选择，记住状态时间较长，反之较短）
     * @param model         传递数据给DAO
     * @param session       用于获取验证码文本
     * @param response      如果登陆成功，将ticket添加到cookie中，再封装到response里返回给用户，用户保存在本地硬盘里
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response) {

        // 先判断验证码

        //String kaptcha = session.getAttribute("kaptcha").toString();
        String kaptcha = (String) session.getAttribute("kaptcha");
        // 验证码为空或者不相等（不区分大小写）
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            // 告诉login模板
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }

        // 检查账号密码

        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {    // 验证账号密码成功
            // 向浏览器发送包含ticket信息的cookie
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            // 设置cookie生效路径
            cookie.setPath(contextPath);
            // 设置cookie有效期
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {                            // 登陆失败
            // 如果不是username的问题，那么参数可以是null，在前端展示的时候也不会出问题
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }


    // 退出登录

    /**
     *
     * @param ticket    从cookie中获取ticket给service更改dao的数据
     * @return
     */
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        // 分别有GET请求的login和POST请求的login，重定向的时候默认的是GET请求的login
        return "redirect:/login";
    }
}
