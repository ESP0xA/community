package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityIndexApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    // 一个简单邮件发送示例
    @Test
    public void testTextMail() {
        mailClient.sendMail("rpm@live.com", "Spring Mail Test", "Welcome to Community!");
    }

    // 在controller里面很容易返回一个模板，而在测试类中，我们可以直接注入Thymeleaf的一个bean：TemplateEngine，用它来生成动态网页
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHtmlMail() {
        // 用Context给模板传参
        Context context = new Context();
        // 将传给模板的参数放到context中
        context.setVariable("username", "sunday");
        // 调用模板引擎生成动态网页
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("rpm@live.com", "Community HTML mail test", content);
    }
}
