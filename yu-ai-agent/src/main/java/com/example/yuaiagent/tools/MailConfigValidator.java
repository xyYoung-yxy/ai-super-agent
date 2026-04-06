package com.example.yuaiagent.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 纯配置验证类：只打印邮箱配置，不依赖任何其他Bean
 * 启动项目后看控制台输出即可判断配置是否读取成功
 */
//@Configuration
public class MailConfigValidator {

    // 读取主配置的核心参数（加默认值，避免读取失败直接报错）
    @Value("${spring.mail.host:配置未读取到}")
    private String mailHost;

    @Value("${spring.mail.port:配置未读取到}")
    private String mailPort;

    @Value("${spring.mail.username:配置未读取到}")
    private String mailUsername;

    // 读取local文件的password（核心验证项）
    @Value("${spring.mail.password:配置未读取到}")
    private String mailPassword;

    // 读取激活的环境（验证local是否真的激活）
    @Value("${spring.profiles.active:未激活任何环境}")
    private String activeProfile;

    /**
     * 项目启动后立即执行，打印所有配置信息
     */
    @Bean
    public CommandLineRunner printMailConfig() {
        return args -> {
            // 打印分割线，方便查看
            System.out.println("\n========================================");
            System.out.println("========== 邮箱配置读取结果 =============");
            System.out.println("========================================");
            System.out.println("激活的环境: " + activeProfile);
            System.out.println("spring.mail.host: " + mailHost);
            System.out.println("spring.mail.port: " + mailPort);
            System.out.println("spring.mail.username: " + mailUsername);
            System.out.println("spring.mail.password: " + 
                    (mailPassword.equals("配置未读取到") ? "配置未读取到" : "已读取（长度：" + mailPassword.length() + "）"));
            System.out.println("========================================");
            System.out.println("========== 配置验证结论 =================");
            // 给出明确的验证结论
            if (activeProfile.equals("local")) {
                System.out.println("✅ 环境激活正常（已激活local）");
            } else {
                System.out.println("❌ 环境激活失败：当前激活的是[" + activeProfile + "]，需要激活local");
            }
            if (mailHost.equals("smtp.qq.com") && mailUsername.equals("2272909516@qq.com")) {
                System.out.println("✅ 主配置（host/username）读取正常");
            } else {
                System.out.println("❌ 主配置读取失败：请检查application.yml中的mail配置");
            }
            if (!mailPassword.equals("配置未读取到") && mailPassword.length() == 16) {
                System.out.println("✅ local文件的password（授权码）读取正常（16位）");
            } else {
                System.out.println("❌ password读取失败：请检查application-local.yml是否存在/配置正确");
            }
            System.out.println("========================================\n");
        };
    }
}