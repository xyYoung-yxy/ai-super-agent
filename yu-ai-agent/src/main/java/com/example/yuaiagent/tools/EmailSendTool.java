package com.example.yuaiagent.tools;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.ToString;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 邮件发送工具类
 */
@Component
public class EmailSendTool {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:2272909516@qq.com}")
    private String mailUsername;


    public EmailSendTool(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送纯文本邮件
     * @param to 收件人邮箱（多个用逗号分隔）
     * @param subject 邮件主题
     * @param content 邮件正文
     * @return 发送结果
     */
    @Tool(description = "Send a plain text email using QQ mailbox")
    public String sendTextEmail(
            @ToolParam(description = "Recipient email address,multiple emails separated by commas") String to,
            @ToolParam(description = "Subject of the email") String subject,
            @ToolParam(description = "Plain text content of the email") String content){

        // 入参校验
        if(to == null || to.trim().isEmpty()) return "Error sending email: Recipient email address cannot be empty";
        if(subject == null || subject.trim().isEmpty()) return "Error sending email: Email subject cannot be empty";
        if(content == null || content.trim().isEmpty()) return "Error sending email: Email content cannot be empty";

        try {
            // 构建 QQ邮箱邮件对象
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailUsername); // 发件人
            helper.setTo(to.split(",")); // 收件人
            helper.setSubject(subject); // 邮件主题
            helper.setText(content, false); // 邮件内容，true表示支持 html格式

            // 发送邮件
            javaMailSender.send(mimeMessage);
            return "Email sent successfully to:" + to + ", subject:" + subject;
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }

    }
}
