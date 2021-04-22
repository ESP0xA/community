package com.nowcoder.community.controller;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    // 封装cookie示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效路径（不是每个访问路径cookie都生效）
        cookie.setPath("/community/alpha");
        // 设置cookie的生效时间（默认的生效时间是一个session期，也就是关闭浏览器就会失效。）
        // 但是如果我们通常会设置一个长效cookie，浏览器将会把这个长效cookie存储在硬盘中
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);
        return "cookie is set";
    }

    // 接受cookie示例
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "cookie is got";
    }

    // session示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        // spring会自动创建session对象并注入进来，我们可以通过这个对象获取服务端session的相关信息
        // 因为session是始终存放在服务端，所以他可以存放任何类型的数据，也可以存放很多数据。
        // 这一点和cookie尤为不同，cookie需要频繁地在浏览器和服务器之间发送接收，所以只能携带少量数据，且cookie中只能存放字符串类型
        // 数据，以便跨语言识别
        // session 存放在服务端的内存中，当服务重启或者关闭浏览器，session就失效了。
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "session is set";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));

        return "session is got";
    }

    // ajax demo
    // method = RequestMethod.POST      通常情况下，页面需通过异步的方式要给服务器提交一些数据
    // @ResponseBody                    因为是异步请求，服务器并非返回一个网页
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功！");
    }
}
