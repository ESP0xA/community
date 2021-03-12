package com.nowcoder.community.service;

import java.util.*;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    // 注入值

    // 域名
    @Value("${community.path.domain}")
    private String domain;
    // 项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 用户注册服务
    /**
     * 点击注册按钮会传入User对象
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {

        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 验证注册信息

        // 验证账号是否存在
        User existedUser = userMapper.selectByName(user.getUsername());
        if (existedUser != null) {
            System.out.println(existedUser);
            map.put("usernameMsg", "账号已存在！");
            return map;
        }

        // 验证邮箱是否存在
        existedUser = userMapper.selectByEmail(user.getEmail());
        if (existedUser != null) {
            map.put("emailMsg", "邮箱已存在！");
            return map;
        }

        // 注册用户

        // 生成用户对象属性
        // 取随机字符串中的前5位
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        // 将用户对象插入数据库
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        // 传入用户邮箱
        context.setVariable("email", user.getEmail());
        // 生成激活url
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        // 如果注册成功，map为空
        return map;
    }


    // 用户激活服务
    /**
     * 点击激活链接的时候会传进来如下两个参数
     * @param userId    用户Id
     * @param code      用户激活码
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);

        if (user.getStatus() == 1) {    // 激活状态status初始化为0，表示未激活状态，如果为1表示已激活
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {     // 验证激活码是否正确
            // 正确则更改激活状态
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {    // 否则验证失败
            return ACTIVATION_FAILURE;
        }
    }

    // 用户登录服务
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public Map<String, Object> login(String username, String password, int expiredSecond) {

        Map<String, Object> map = new HashMap<>();

        // 空值处理

        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }


        // 账号合法性验证

        // 检查账号是否存在
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        // 验证账号是否已经激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("pawwordMsg", "密码不正确！");
            return map;
        }


        // 账号密码合法，生成登陆凭证

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecond * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        // 登陆成功
        map.put("ticket", loginTicket.getTicket());

        return map;
    }
}
