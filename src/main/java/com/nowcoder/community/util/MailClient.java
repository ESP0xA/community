package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    // JavaMailSender的bean是被IOC容器直接管理的
    @Autowired
    private JavaMailSender javaMailSender;

    // 发件人
    // 通过application.properties的key将值注入到当前Bean中
    @Value("${spring.mail.username}")
    private String from;

    // 邮件发送逻辑
    public void sendMail(String to, String subject, String content) {
        try {
            // 创建一个message对象
            MimeMessage message = javaMailSender.createMimeMessage();

            // 构建message
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 内容设置为支持html文本
            helper.setText(content, true);

            // 发送邮件
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            // 如果出现异常用Logger记录日志
            logger.error("发送邮件失败：" + e.getMessage());
        }

    }

}
