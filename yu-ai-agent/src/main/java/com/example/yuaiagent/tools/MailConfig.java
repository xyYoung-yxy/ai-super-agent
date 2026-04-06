package com.example.yuaiagent.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

//@Configuration
public class MailConfig {

    @Value("${spring.mail.host:smtp.qq.com}")
    private String host;


    @Value("${spring.mail.port:465}")
    private String portStr;

    @Value("${spring.mail.username:2272909516@qq.com}")
    private String username;

    @Value("${spring.mail.password:}") // 授权码允许空（启动时验证）
    private String password;
    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(portStr));
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = new Properties();
        // 465端口强制使用SSL（关键！缺失则连接被断开）
        props.put("mail.smtp.ssl.enable", "true");
        // 强制使用SSL协议（对应配置中的smtps）
        props.put("mail.transport.protocol", "smtps");
        // 开启认证（必填）
        props.put("mail.smtp.auth", "true");
        // 增加连接超时，避免快速断开
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        // 测试时开启debug，能看到SMTP交互日志
        props.put("mail.debug", "true");

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }
}