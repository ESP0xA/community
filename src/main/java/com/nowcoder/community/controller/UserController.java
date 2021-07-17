package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 注入上传路径
    @Value("${community.path.upload}")
    private String uploadPath;

    // 注入域名
    @Value("${community.path.domain}")
    private String domain;

    // 注入项目访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 注入UserService
    @Autowired
    private UserService userService;

    // 注入user容器HostHolder
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting"; // setting.html
    }


    /**
     * 上传用户头像
     * @param headerImage   Spring MVC提供的一个专门的处理文件的类型
     * @param model         封装数据给View
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        // 为了避免图片重名，需要将图片命名，但不改变后缀

        // 获取文件名
        String fileName = headerImage.getOriginalFilename();
        // 获取图片文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 如果后缀是空，处理一下
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放路径（全限定名），此时是空文件
        File dst = new File(uploadPath + "/" + fileName);
        // 将上传的文件数据写入文件
        try {
            headerImage.transferTo(dst);
        } catch (IOException e) {
            logger.error("上传文件失败： " + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 上传成功，更新当前用户头像路径（web访问路径）
        // http://localHost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }


    /**
     * 获取用户头像
     * @param fileName  从访问路径中获取文件名
     * @param response  用response对象向页面写入二进制数据
     * @return 因为不用返回页面或者模板，而是直接向页面写入二进制数据，所以返回void
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)    // 注意当前类的访问路径为user
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放的路径（全限定名）
        fileName = uploadPath + "/" + fileName;
        // 向浏览器写入数据，首先要知道文件类型，通过后缀来判断
        String suffix = fileName.substring(fileName.indexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix); // ?????
        // IO操作
        try (
                // 读取文件得到输入流
                // 输入流是我们自己创建的，所以需要手动关闭
                // 将FileInputStream写在try小括号中，会自动加入在finally中关闭，当且仅当FileInputStream存在close方法
                FileInputStream fis = new FileInputStream(fileName);
                ){
            // 输出
            // response是由Spring，所以会自动关闭
            OutputStream os = response.getOutputStream();
            // 先声明一个缓冲区
            byte[] buffer = new byte[1024];

            // b == -1 说明没有读到数据
            int b = fis.read(buffer);
            for (; b != -1; b = fis.read(buffer)) {
                // 一次写入buffer这么多数据，从0开始，到b结束
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }


    @LoginRequired
    @RequestMapping(path = "/password", method = RequestMethod.POST)
    //@ResponseBody
    public String updatePassword(Model model, HttpServletRequest request, String curPassword, String newPassword, String newPasswordConfirm) {
        //Map<String, Object> map = userService.updatePassword()
        // 非法判断
        // 原密码不做判断，直接查询是否正确
        // 新密码先判断两次输入是否一致，在判断长度是否大于等于8位，
        if (newPassword.length() < 8) {
            model.addAttribute("newPasswordLengthMsg", "密码长度不能小于8位!");
            return "/site/setting";
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("newPasswordNotMatchMsg", "两次密码输入不一致！");
            return "/site/setting";
        }

        if (hostHolder == null) return "/site/login";                                                                   // 未登录，事实上未登录用户会被拦截器拒绝请求，此处功能冗余，但更保险

        User user = hostHolder.getUser();

        // 有效的登陆状态
        if(!userService.checkPassword(user, curPassword)) {                                                             // 密码不正确
            model.addAttribute("passwordNotCorrectMsg", "密码不正确！");
            return "/site/setting";
        }
        // 密码正确，更新密码
        userService.updatePassword(user, newPassword);
        // 退出登录
        return "redirect:/logout";
    }

    // 个人主页（点击每个用户的头像都能进入他的个人主页）
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) { // 防止攻击
            throw new RuntimeException("该用户不存在！");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞用户的数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        return "/site/profile";
    }
}
